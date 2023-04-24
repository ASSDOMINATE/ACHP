package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.DateUtil;
import com.hwja.tool.utils.SqlUtil;
import org.dominate.achp.common.cache.CardCache;
import org.dominate.achp.entity.BaseUserRecord;
import org.dominate.achp.mapper.BaseUserRecordMapper;
import org.dominate.achp.service.IBaseUserRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 用户基础记录 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class BaseUserRecordServiceImpl extends ServiceImpl<BaseUserRecordMapper, BaseUserRecord> implements IBaseUserRecordService {

    @Override
    public BaseUserRecord getDailyRecord(int accountId) {
        BaseUserRecord record = CardCache.getUserDailyRecord(accountId);
        if (null != record) {
            return record;
        }
        Date time = new Date();
        QueryWrapper<BaseUserRecord> query = new QueryWrapper<>();
        query.lambda().eq(BaseUserRecord::getAccountId, accountId)
                .between(BaseUserRecord::getCreateTime, DateUtil.getStartDate(time), DateUtil.getFinallyDate(time))
                .last(SqlUtil.limitOne());
        // 不存在就创建一条
        if (0 == count(query)) {
            record = new BaseUserRecord();
            record.setAccountId(accountId);
            Date baseRequestTime = new Date(System.currentTimeMillis() - 1000 * 60 * 5);
            record.setLatestRequestTime(baseRequestTime);
            record.setDailyRequestCount(0);
            save(record);
        }
        record = getOne(query);
        CardCache.saveUserRecord(record);
        return record;
    }


}
