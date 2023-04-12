package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dominate.achp.entity.ChatSceneItem;
import org.dominate.achp.mapper.ChatSceneItemMapper;
import org.dominate.achp.service.IChatSceneItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 对话场景项 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class ChatSceneItemServiceImpl extends ServiceImpl<ChatSceneItemMapper, ChatSceneItem> implements IChatSceneItemService {

    @Override
    public List<ChatSceneItem> list(int sceneId) {
        QueryWrapper<ChatSceneItem> query = new QueryWrapper<>();
        query.lambda()
                .eq(ChatSceneItem::getSceneId, sceneId)
                .eq(ChatSceneItem::getDel, false)
                .orderByAsc(ChatSceneItem::getSeq);
        return list(query);
    }
}
