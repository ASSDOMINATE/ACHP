package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dominate.achp.entity.ChatSceneConf;
import org.dominate.achp.mapper.ChatSceneConfMapper;
import org.dominate.achp.service.IChatSceneConfService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 对话场景配置，把场景项拼接为文字的配置 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class ChatSceneConfServiceImpl extends ServiceImpl<ChatSceneConfMapper, ChatSceneConf> implements IChatSceneConfService {

    @Override
    public List<ChatSceneConf> list(int sceneId) {
        QueryWrapper<ChatSceneConf> query = new QueryWrapper<>();
        query.lambda()
                .eq(ChatSceneConf::getSceneId, sceneId)
                .eq(ChatSceneConf::getDel, false)
                .orderByAsc(ChatSceneConf::getSeq);
        return list(query);
    }
}
