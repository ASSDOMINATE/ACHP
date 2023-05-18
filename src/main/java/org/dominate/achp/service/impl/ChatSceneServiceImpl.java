package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import org.dominate.achp.entity.ChatScene;
import org.dominate.achp.entity.dto.SceneDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.entity.wrapper.SceneWrapper;
import org.dominate.achp.mapper.ChatSceneMapper;
import org.dominate.achp.service.IChatSceneService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 对话场景 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Service
public class ChatSceneServiceImpl extends ServiceImpl<ChatSceneMapper, ChatScene> implements IChatSceneService {

    @Override
    public List<SceneDTO> list(PageReq page) {
        QueryWrapper<ChatScene> query = new QueryWrapper<>();
        query.lambda().eq(ChatScene::getDel, false)
                .eq(ChatScene::getForRecommend, false)
                .orderByAsc(ChatScene::getSeq)
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()));
        return dtoList(query);
    }

    @Override
    public List<SceneDTO> list(List<Integer> sceneIdList) {
        if (CollectionUtils.isEmpty(sceneIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<ChatScene> query = new QueryWrapper<>();
        query.lambda().in(ChatScene::getId, sceneIdList);
        return dtoList(query);
    }

    @Override
    public String getSystem(int id) {
        QueryWrapper<ChatScene> query = new QueryWrapper<>();
        query.lambda().eq(ChatScene::getId, id).select(ChatScene::getSetSystem);
        return getOne(query).getSetSystem();
    }

    private List<SceneDTO> dtoList(QueryWrapper<ChatScene> query) {
        List<ChatScene> sceneList = list(query);
        return SceneWrapper.build().entitySceneDTO(sceneList);
    }
}
