package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.dominate.achp.common.enums.CardRecordState;
import org.dominate.achp.common.enums.CardType;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.common.helper.CardHelper;
import org.dominate.achp.common.utils.UniqueCodeUtil;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.entity.wrapper.CardWrapper;
import org.dominate.achp.mapper.BaseCardRecordMapper;
import org.dominate.achp.service.IBaseCardRecordService;
import org.dominate.achp.service.IBaseCardService;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 付费卡密记录 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@AllArgsConstructor
@Service
public class BaseCardRecordServiceImpl extends ServiceImpl<BaseCardRecordMapper, BaseCardRecord> implements IBaseCardRecordService {

    private final IBaseCardService baseCardService;

    @Override
    public List<CardRecordDTO> userRecordList(int accountId) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .orderByDesc(BaseCardRecord::getUpdateTime);
        List<BaseCardRecord> recordList = list(query);
        return CardWrapper.build().entityCardRecordDTO(recordList);
    }

    @Override
    public List<CardRecordDTO> cardRecordList(int cardId, PageReq page) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getCardId, cardId)
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()))
                .orderByDesc(BaseCardRecord::getId);
        List<BaseCardRecord> recordList = list(query);
        return CardWrapper.build().entityCardRecordDTO(recordList);
    }

    @Override
    public CardRecordDTO checkUserRecord(int accountId) throws BusinessException {
        CardRecordDTO cardRecord = CardHelper.getUsingCard(accountId);
        // 1.缓存中有记录，检查是否过期
        if (null != cardRecord) {
            if (checkRecordUsed(cardRecord.getCardTypeCode(), cardRecord.getExpireTime(), cardRecord.getRemainCount())) {
                saveRecordUsed(accountId, cardRecord.getId(), cardRecord.getRequestCount(), cardRecord.getRemainCount());
//                throw BusinessException.create(ExceptionType.SEND_CARD_LIMIT);
                //TODO 前端问题暂时隐藏报错
                throw BusinessException.create(ExceptionType.EMPTY_ERROR);
            }
            return cardRecord;
        }
        // 2.查询数据库中用户是否有记录
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getAccountId, accountId);
        // 从来没有开通过
        if (0 == count(query)) {
//            throw BusinessException.create(ExceptionType.NOT_BUY_USING);
            //TODO 前端问题暂时隐藏报错
            throw BusinessException.create(ExceptionType.EMPTY_ERROR);
        }
        // 3.查询可用的记录
        query.lambda().eq(BaseCardRecord::getState, CardRecordState.USING.getCode())
                .last(SqlUtil.limitOne());
        BaseCardRecord record = getOne(query);
        if (null != record) {
            cardRecord = CardWrapper.build().entityDTO(record);
            CardHelper.saveUsingCard(accountId, cardRecord);
            // 重新执行 checkUserRecord->1 进行状态检查
            return checkUserRecord(accountId);
        }
        // 4.查找待使用的卡
        QueryWrapper<BaseCardRecord> waitQuery = new QueryWrapper<>();
        waitQuery.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .eq(BaseCardRecord::getState, CardRecordState.WAIT.getCode())
                .last(SqlUtil.limitOne());
        record = getOne(waitQuery);
        if (null != record) {
            // 设置为启用后清理缓存
            BaseCard card = baseCardService.getById(record.getCardId());
            if (saveRecordUsing(accountId, record.getId(), card)) {
                setUsing(record, card);
                cardRecord = CardWrapper.build().entityDTO(record);
                CardHelper.saveUsingCard(accountId, cardRecord);
                // 才设置为启用的卡不需要检查，直接返回即可
                return cardRecord;
            }
        }
//            throw BusinessException.create(ExceptionType.NOT_CARD_USING);
        //TODO 前端问题暂时隐藏报错
        throw BusinessException.create(ExceptionType.EMPTY_ERROR);

    }

    @Override
    public String getRecordWaitInfo(int accountId) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .eq(BaseCardRecord::getState, CardRecordState.WAIT.getCode())
                .select(BaseCardRecord::getCardName);
        List<BaseCardRecord> recordList = list(query);
        if (CollectionUtils.isEmpty(recordList)) {
            return StringUtil.EMPTY;
        }
        StringBuilder info = new StringBuilder();
        info.append(",待使用的：");
        for (int i = 0; i < recordList.size(); i++) {
            if(0 != i){
                info.append("，");
            }
            info.append(recordList.get(i).getCardName());
        }
        return info.toString();
    }

    private static boolean checkRecordUsed(int cardType, long expireTime, int remainCount) {
        switch (CardType.getValueByCode(cardType)) {
            case DAY:
                // 已过期
                return expireTime < System.currentTimeMillis();
            case COUNT:
                // 次数使用完
                return remainCount <= 0;
            default:
                return true;
        }
    }

    @Override
    public boolean hasBinding(int accountId) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .eq(BaseCardRecord::getState, CardRecordState.USING.getCode());
        return count(query) > 0;
    }

    @Override
    public boolean bindRecord(int accountId, int id, BaseCard card) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .eq(BaseCardRecord::getState, CardRecordState.USING.getCode());
        // 当前有启用的卡，设置为待使用
        if (0 < count(query)) {
            BaseCardRecord update = new BaseCardRecord();
            update.setId(id);
            update.setAccountId(accountId);
            update.setState(CardRecordState.WAIT.getCode());
            return updateById(update);
        }
        return saveRecordUsing(accountId, id, card);
    }

    private boolean saveRecordUsing(int accountId, int recordId, BaseCard card) {
        BaseCardRecord update = new BaseCardRecord();
        update.setId(recordId);
        update.setAccountId(accountId);
        setUsing(update, card);
        CardHelper.removeUsingCard(accountId);
        return updateById(update);
    }

    private static void setUsing(BaseCardRecord record, BaseCard card) {
        switch (CardType.getValueByCode(card.getType())) {
            case DAY:
                Date start = new Date();
                Date expire = DateUtils.addDays(start, card.getDayLimit());
                record.setStartTime(start);
                record.setExpireTime(expire);
                break;
            case COUNT:
                record.setRemainCount(card.getCountLimit());
                break;
            default:
                break;
        }
        record.setState(CardRecordState.USING.getCode());
    }

    @Override
    public boolean bindRecord(int accountId, BaseCard card) {
        int recordId = createRecord(card);
        return bindRecord(accountId, recordId, card);
    }

    @Override
    public int createRecord(BaseCard card) {
        BaseCardRecord record = new BaseCardRecord();
        record.setCardId(card.getId());
        record.setCardName(card.getName());
        record.setExchangeKey(UniqueCodeUtil.createExchangeKey());
        record.setCardType(card.getType());
        record.setRemainCount(card.getCountLimit());
        record.setRequestCount(0);
        Date emptyTime = new Date(0);
        record.setStartTime(emptyTime);
        record.setExpireTime(emptyTime);
        record.setState(CardRecordState.NOT_USE.getCode());
        save(record);
        return record.getId();
    }

    @Override
    public BaseCardRecord findActiveRecord(String code) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getExchangeKey, code)
                .eq(BaseCardRecord::getState, CardRecordState.NOT_USE.getCode())
                .last(SqlUtil.limitOne());
        return getOne(query);
    }

    @Override
    public boolean saveRecordUsed(int accountId, int id, int requestCount, int remainCount) {
        BaseCardRecord update = new BaseCardRecord();
        update.setId(id);
        update.setRequestCount(requestCount);
        update.setRemainCount(remainCount);
        update.setState(CardRecordState.USED.getCode());
        if (updateById(update)) {
            CardHelper.removeUsingCard(accountId);
            return true;
        }
        return false;
    }

}
