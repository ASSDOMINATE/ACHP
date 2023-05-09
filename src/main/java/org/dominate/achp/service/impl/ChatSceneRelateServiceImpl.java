package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwja.tool.utils.SqlUtil;
import org.dominate.achp.entity.ChatSceneRelate;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.mapper.ChatSceneRelateMapper;
import org.dominate.achp.service.IChatSceneRelateService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 对话场景关联 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Service
public class ChatSceneRelateServiceImpl extends ServiceImpl<ChatSceneRelateMapper, ChatSceneRelate> implements IChatSceneRelateService {

    @Override
    public List<Integer> getSceneIdList(int categoryId, PageReq page) {
        QueryWrapper<ChatSceneRelate> query = new QueryWrapper<>();
        query.lambda().eq(ChatSceneRelate::getCategoryId, categoryId)
                .eq(ChatSceneRelate::getDel, false)
                .select(ChatSceneRelate::getSceneId)
                .orderByDesc(ChatSceneRelate::getId)
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()));
        List<ChatSceneRelate> relateList = list(query);
        List<Integer> sceneIdList = new ArrayList<>(relateList.size());
        for (ChatSceneRelate relate : relateList) {
            sceneIdList.add(relate.getSceneId());
        }
        return sceneIdList;
    }

    @Override
    public List<Integer> getCategoryIdList(int sceneId) {
        QueryWrapper<ChatSceneRelate> query = new QueryWrapper<>();
        query.lambda().eq(ChatSceneRelate::getSceneId, sceneId)
                .eq(ChatSceneRelate::getDel, false)
                .select(ChatSceneRelate::getCategoryId);
        List<ChatSceneRelate> relateList = list(query);
        List<Integer> categoryIdList = new ArrayList<>(relateList.size());
        for (ChatSceneRelate relate : relateList) {
            categoryIdList.add(relate.getCategoryId());
        }
        return categoryIdList;
    }

    @Override
    public List<ChatSceneRelate> getRelateList(int targetId, boolean forScene) {
        QueryWrapper<ChatSceneRelate> query = new QueryWrapper<>();
        query.lambda().eq(forScene, ChatSceneRelate::getSceneId, targetId)
                .eq(!forScene, ChatSceneRelate::getCategoryId, targetId)
                .eq(ChatSceneRelate::getDel, false);
        return list(query);
    }

}
