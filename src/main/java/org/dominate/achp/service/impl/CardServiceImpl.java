package org.dominate.achp.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.enums.CardType;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.common.helper.CardHelper;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.BaseUserRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.service.CardService;
import org.dominate.achp.service.IBaseCardRecordService;
import org.dominate.achp.service.IBaseConfigService;
import org.dominate.achp.service.IBaseUserRecordService;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private final IBaseUserRecordService baseUserRecordService;
    private final IBaseCardRecordService baseCardRecordService;
    private final IBaseConfigService baseConfigService;

    @Override
    public void checkSendLimit(int accountId) {
        BaseConfig config = baseConfigService.current();
        BaseUserRecord userRecord = baseUserRecordService.getDailyRecord(accountId);
        boolean isFaster = !checkFreq(config.getFreqSecondLimit(), userRecord.getLatestRequestTime());
        // 超过频率限制，且不是会员
        if (isFaster) {
            if (!baseCardRecordService.hasBinding(accountId)) {
                throw BusinessException.create(ExceptionType.SEND_FREQ_LIMIT);
            }
        }
        // 未超过每日请求限制
        if (config.getDailyRequestLimit() >= userRecord.getDailyRequestCount()) {
            return;
        }
        try {
            // 检查会员信息 无异常检查通过
            baseCardRecordService.checkUserRecord(accountId);
        } catch (BusinessException e) {
            throw BusinessException.create(ExceptionType.SEND_COUNT_LIMIT);
        }
    }


    @Override
    public void addUserRequestRecord(int accountId) {
        BaseUserRecord record = baseUserRecordService.getDailyRecord(accountId);
        record.setDailyRequestCount(record.getDailyRequestCount() + 1);
        record.setLatestRequestTime(new Date());
        CardHelper.saveUserRecord(record);
        CardHelper.setUserRecordUpdate(accountId);
        BaseConfig config = baseConfigService.current();
        // 先把免费的次数用完
        if (config.getDailyRequestLimit() >= record.getDailyRequestCount()) {
            return;
        }
        try {
            CardRecordDTO cardRecord = baseCardRecordService.checkUserRecord(accountId);
            if (CardType.COUNT.getCode() != cardRecord.getCardTypeCode()) {
                return;
            }
            cardRecord.setRequestCount(cardRecord.getRequestCount() + 1);
            cardRecord.setRemainCount(cardRecord.getRemainCount() - 1);
            CardHelper.saveUsingCard(accountId, cardRecord);
            CardHelper.setUserUsingUpdate(accountId);
        } catch (Exception e) {
            log.info("已达请求限制 用户ID {} ", accountId);
        }
    }

    private static boolean checkFreq(int freqSecond, Date requestDate) {
        // 当前时间 - 间隔时间 > 上一次请求时间 代表没达到限制
        return (System.currentTimeMillis() - freqSecond * 1000L) > requestDate.getTime();
    }


}
