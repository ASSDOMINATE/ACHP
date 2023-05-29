package org.dominate.achp.schedule;

import com.hwja.tool.utils.LoadUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.cache.ChatCache;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.BaseUserRecord;
import org.dominate.achp.entity.dto.CardRecordDTO;
import org.dominate.achp.service.IBaseCardRecordService;
import org.dominate.achp.service.IBaseUserRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录更新
 *
 * @author dominate
 * @since 2023-04-14
 */
@Slf4j
@Component
@AllArgsConstructor
public class RecordUpdate {

    private final IBaseCardRecordService baseCardRecordService;
    private final IBaseUserRecordService baseUserRecordService;

    private static final int UPDATE_LIST_MAX = 5000;


    @Scheduled(cron = "0 0 * * * ?")
    public void updateSendRecord() {
        if(!LoadUtil.onProd()){
            return;
        }
        updateCardRecord();
        updateUserRecord();
    }

    private void updateCardRecord() {
        long listLength = ChatCache.getUpdateUserUsingLength();
        if (0 == listLength) {
            return;
        }
        int targetLength;
        if (listLength > UPDATE_LIST_MAX) {
            targetLength = UPDATE_LIST_MAX;
        } else {
            targetLength = (int) listLength;
        }
        List<BaseCardRecord> updateList = new ArrayList<>(targetLength);
        for (int i = 0; i < targetLength; i++) {
            CardRecordDTO cardRecord = ChatCache.getUpdateUserUsing();
            BaseCardRecord update = new BaseCardRecord();
            update.setId(cardRecord.getId());
            update.setRequestCount(cardRecord.getRequestCount());
            update.setRemainCount(cardRecord.getRemainCount());
            updateList.add(update);
        }
        baseCardRecordService.updateBatchById(updateList);
    }


    private void updateUserRecord() {
        long listLength = ChatCache.getUpdateUserDailyLength();
        if (0 == listLength) {
            return;
        }
        int targetLength;
        if (listLength > UPDATE_LIST_MAX) {
            targetLength = UPDATE_LIST_MAX;
        } else {
            targetLength = (int) listLength;
        }
        List<BaseUserRecord> updateList = new ArrayList<>(targetLength);
        for (int i = 0; i < targetLength; i++) {
            BaseUserRecord userRecord = ChatCache.getUpdateUserDaily();
            BaseUserRecord update = new BaseUserRecord();
            update.setId(userRecord.getId());
            update.setDailyRequestCount(userRecord.getDailyRequestCount());
            update.setLatestRequestTime(userRecord.getLatestRequestTime());
            updateList.add(update);
        }
        baseUserRecordService.updateBatchById(updateList);
    }
}
