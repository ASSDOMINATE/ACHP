package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.BaseUserRecord;

/**
 * <p>
 * 用户基础记录 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IBaseUserRecordService extends IService<BaseUserRecord> {

    /**
     * 获取每日记录
     * 使用缓存
     *
     * @param accountId 账号ID
     * @return 每日记录
     */
    BaseUserRecord getDailyRecord(int accountId);

}
