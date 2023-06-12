package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dominate.achp.common.cache.StatisticCache;
import org.dominate.achp.common.enums.SceneCountType;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.entity.ChatGroup;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.dto.GroupDTO;
import org.dominate.achp.entity.wrapper.ChatWrapper;
import org.dominate.achp.mapper.ChatGroupMapper;
import org.dominate.achp.service.IChatGroupService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 对话组 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class ChatGroupServiceImpl extends ServiceImpl<ChatGroupMapper, ChatGroup> implements IChatGroupService {

    @Override
    public boolean isExisted(String id) {
        QueryWrapper<ChatGroup> query = new QueryWrapper<>();
        query.lambda().eq(ChatGroup::getId, id);
        return count(query) > 0;
    }

    @Override
    public boolean checkGroup(ChatDTO chat, int accountId) {
        if (isExisted(chat.getChatGroupId())) {
            return true;
        }
        StatisticCache.addSceneCount(chat.getSceneId(), SceneCountType.CHAT);
        ChatGroup group = new ChatGroup();
        group.setId(chat.getChatGroupId());
        group.setAccountId(accountId);
        group.setTitle(ChatGptHelper.extractTitle(chat.getSentence()));
        return save(group);
    }

    @Override
    public List<GroupDTO> list(List<String> groupIdList) {
        if (CollectionUtils.isEmpty(groupIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<ChatGroup> query = new QueryWrapper<>();
        query.lambda().in(ChatGroup::getId, groupIdList)
                .select(ChatGroup::getId, ChatGroup::getAccountId, ChatGroup::getTitle);
        List<ChatGroup> groupList = list(query);
        return ChatWrapper.build().entityGroupDTO(groupList);
    }
}
