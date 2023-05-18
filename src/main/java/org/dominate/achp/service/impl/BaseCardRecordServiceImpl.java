package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.ChatCache;
import org.dominate.achp.common.enums.CardRecordState;
import org.dominate.achp.common.enums.CardType;
import org.dominate.achp.common.utils.UniqueCodeUtil;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.entity.wrapper.CardWrapper;
import org.dominate.achp.mapper.BaseCardRecordMapper;
import org.dominate.achp.service.IBaseCardRecordService;
import org.dominate.achp.service.IBaseCardService;
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
        info.append("；待使用的有：");
        for (int i = 0; i < recordList.size(); i++) {
            if (0 != i) {
                info.append("，");
            }
            info.append(recordList.get(i).getCardName());
        }
        return info.toString();
    }


    @Override
    public boolean hasUsingBinding(int accountId) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .eq(BaseCardRecord::getState, CardRecordState.USING.getCode());
        return count(query) > 0;
    }

    @Override
    public boolean bindRecord(int accountId, int id, BaseCard card) {
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .eq(BaseCardRecord::getState, CardRecordState.USING.getCode())
                .last(SqlUtil.limitOne());
        // 当前有启用的卡，设置为待使用
        if (count(query) > 0) {
            BaseCardRecord record = getOne(query);
            if (CardType.checkRecordUsed(record.getCardType(), record.getExpireTime().getTime(), record.getRemainCount())) {
                BaseCardRecord update = new BaseCardRecord();
                update.setId(id);
                update.setAccountId(accountId);
                update.setState(CardRecordState.WAIT.getCode());
                return updateById(update);
            }
        }
        return saveRecordUsing(accountId, id, card);
    }

    @Override
    public boolean saveRecordUsing(int accountId, int id, BaseCard card) {
        BaseCardRecord update = new BaseCardRecord();
        update.setId(id);
        update.setAccountId(accountId);
        CardRecordState.setUsing(update, card);
        ChatCache.removeUsingCard(accountId);
        return updateById(update);
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
            ChatCache.removeUsingCard(accountId);
            return true;
        }
        return false;
    }

}
