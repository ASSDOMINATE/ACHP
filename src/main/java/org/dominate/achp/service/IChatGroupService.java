package org.dominate.achp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dominate.achp.entity.ChatGroup;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.dto.GroupDTO;

import java.util.List;

/**
 * <p>
 * 对话组 服务类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
public interface IChatGroupService extends IService<ChatGroup> {

    /**
     * 对话组是否存在
     *
     * @param id 对话组ID
     * @return 是否存在
     */
    boolean isExisted(String id);

    /**
     * 确认对话组 无对话组会创建新对话组
     *
     * @param chat      对话消息
     * @param accountId 账号ID
     * @return 确认可用
     */
    boolean checkGroup(ChatDTO chat, int accountId);


    List<GroupDTO> list(List<String> groupIdList);
}
