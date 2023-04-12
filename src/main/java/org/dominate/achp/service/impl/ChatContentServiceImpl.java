package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dominate.achp.entity.ChatContent;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.wrapper.ChatWrapper;
import org.dominate.achp.mapper.ChatContentMapper;
import org.dominate.achp.service.IChatContentService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 聊天内容 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class ChatContentServiceImpl extends ServiceImpl<ChatContentMapper, ChatContent> implements IChatContentService {

    @Override
    public List<ContentDTO> list(List<Integer> contentIdList) {
        if (CollectionUtils.isEmpty(contentIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<ChatContent> query = new QueryWrapper<>();
        query.lambda().in(ChatContent::getId, contentIdList)
                .select(ChatContent::getId, ChatContent::getLastId, ChatContent::getReply, ChatContent::getSentence,
                        ChatContent::getCreateTime)
                .orderByAsc(ChatContent::getId);
        List<ChatContent> contentList = list(query);
        return ChatWrapper.build().entityContentDTO(contentList);
    }
}
