package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwja.tool.utils.SqlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.cache.ChatCache;
import org.dominate.achp.common.enums.CardRecordState;
import org.dominate.achp.common.enums.CardType;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.BaseUserRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.entity.wrapper.CardWrapper;
import org.dominate.achp.service.*;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 卡密相关逻辑 实现类
 *
 * @author dominate
 * @since 2023-04-27
 */
@Slf4j
@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private final IBaseUserRecordService baseUserRecordService;
    private final IBaseCardRecordService baseCardRecordService;
    private final IBaseCardService baseCardService;
    private final IBaseConfigService baseConfigService;

    @Override
    public void checkSendLimit(int accountId) {
        BaseConfig config = baseConfigService.current();
        BaseUserRecord userRecord = baseUserRecordService.getDailyRecord(accountId);
        boolean isFaster = !checkFreq(config.getFreqSecondLimit(), userRecord.getLatestRequestTime());
        // 超过频率限制，且不是会员
        if (isFaster) {
            if (!baseCardRecordService.hasUsingBinding(accountId)) {
                throw BusinessException.create(ExceptionType.SEND_FREQ_LIMIT);
            }
        }
        // 未超过每日请求限制
        if (config.getDailyRequestLimit() >= userRecord.getDailyRequestCount()) {
            return;
        }
        try {
            // 检查会员信息 无异常检查通过
            checkUserRecord(accountId);
        } catch (BusinessException e) {
            throw BusinessException.create(ExceptionType.SEND_COUNT_LIMIT);
        }
    }


    @Override
    public void addUserRequestRecord(int accountId) {
        BaseUserRecord record = baseUserRecordService.getDailyRecord(accountId);
        record.setDailyRequestCount(record.getDailyRequestCount() + 1);
        record.setLatestRequestTime(new Date());
        ChatCache.saveUserDaily(record);
        ChatCache.setUserDailyUpdate(accountId);
        BaseConfig config = baseConfigService.current();
        // 先把免费的次数用完
        if (config.getDailyRequestLimit() >= record.getDailyRequestCount()) {
            return;
        }
        try {
            CardRecordDTO cardRecord = checkUserRecord(accountId);
            if (CardType.COUNT.getCode() != cardRecord.getCardTypeCode()) {
                return;
            }
            cardRecord.setRequestCount(cardRecord.getRequestCount() + 1);
            cardRecord.setRemainCount(cardRecord.getRemainCount() - 1);
            ChatCache.saveUsingCard(accountId, cardRecord);
            ChatCache.setUserUsingUpdate(accountId);
        } catch (Exception e) {
            log.info("已达请求限制 用户ID {} ", accountId);
        }
    }


    @Override
    public CardRecordDTO checkUserRecord(int accountId) throws BusinessException {
        CardRecordDTO cardRecord = ChatCache.getUsingCard(accountId);
        // 1.缓存中有记录，检查是否过期
        if (null != cardRecord) {
            if (CardType.checkRecordUsed(cardRecord.getCardTypeCode(), cardRecord.getExpireTime(), cardRecord.getRemainCount())) {
                baseCardRecordService.saveRecordUsed(accountId, cardRecord.getId(), cardRecord.getRequestCount(), cardRecord.getRemainCount());
                // 如果过期，尝试启用待使用的卡
                cardRecord = startWaitingCard(accountId);
                if (null != cardRecord) {
                    return cardRecord;
                }

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
        if (0 == baseCardRecordService.count(query)) {

//            throw BusinessException.create(ExceptionType.NOT_BUY_USING);
//TODO 前端问题暂时隐藏报错

            throw BusinessException.create(ExceptionType.EMPTY_ERROR);
        }
        // 3.查询可用的记录
        query.lambda().eq(BaseCardRecord::getState, CardRecordState.USING.getCode())
                .last(SqlUtil.limitOne());
        BaseCardRecord record = baseCardRecordService.getOne(query);
        if (null != record) {
            return parseAndSaveCache(accountId, record);
        }
        // 4.启用待使用的卡
        cardRecord = startWaitingCard(accountId);
        if (null != cardRecord) {
            return cardRecord;
        }
        // 5.没有可用卡密

//            throw BusinessException.create(ExceptionType.NOT_CARD_USING);
//TODO 前端问题暂时隐藏报错

        throw BusinessException.create(ExceptionType.EMPTY_ERROR);
    }

    private CardRecordDTO startWaitingCard(int accountId) {
        // 4.查找待使用的卡
        QueryWrapper<BaseCardRecord> waitQuery = new QueryWrapper<>();
        waitQuery.lambda().eq(BaseCardRecord::getAccountId, accountId)
                .eq(BaseCardRecord::getState, CardRecordState.WAIT.getCode())
                .last(SqlUtil.limitOne());
        BaseCardRecord record = baseCardRecordService.getOne(waitQuery);
        if (null == record) {
            return null;
        }
        // 设置为启用后清理缓存
        BaseCard card = baseCardService.getById(record.getCardId());
        if (baseCardRecordService.saveRecordUsing(accountId, record.getId(), card)) {
            CardRecordState.setUsing(record, card);
            return parseAndSaveCache(accountId, record);
        }
        return null;
    }

    private static CardRecordDTO parseAndSaveCache(int accountId, BaseCardRecord record) {
        CardRecordDTO cardRecord = CardWrapper.build().entityDTO(record);
        ChatCache.saveUsingCard(accountId, cardRecord);
        return cardRecord;
    }

    private static boolean checkFreq(int freqSecond, Date requestDate) {
        // 当前时间 - 间隔时间 > 上一次请求时间 代表没达到限制
        return (System.currentTimeMillis() - freqSecond * 1000L) > requestDate.getTime();
    }


}
