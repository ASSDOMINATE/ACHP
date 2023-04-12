package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatRecord;
import org.dominate.achp.entity.req.PageReq;

import java.util.List;

/**
 * <p>
 * 对话记录，关联用户-会话组-会话内容-会话场景 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IChatRecordService extends IService<ChatRecord> {

    /**
     * 获取上一条对话ID
     *
     * @param groupId 对话组ID
     * @return 上一条对话ID
     */
    int getLastContentId(String groupId);

    /**
     * 获取用户对话组ID列表
     *
     * @param accountId 账号ID
     * @param page      分页参数
     * @return 对话组ID列表
     */
    List<String> getUserGroupIdList(int accountId, PageReq page);

    /**
     * 获取用户场景ID列表
     *
     * @param accountId 账号ID
     * @param page      分页参数
     * @return 场景ID列表
     */
    List<Integer> getUserSceneIdList(int accountId, PageReq page);

    /**
     * 获取对话组下内容ID列表
     *
     * @param groupId 对话组ID
     * @param page    分页参数
     * @return 内容ID列表
     */
    List<Integer> getGroupContentIdList(String groupId, PageReq page);

}
