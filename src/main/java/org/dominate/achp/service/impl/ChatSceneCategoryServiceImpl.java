package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dominate.achp.entity.ChatSceneCategory;
import org.dominate.achp.entity.dto.SceneCategoryDTO;
import org.dominate.achp.entity.wrapper.SceneWrapper;
import org.dominate.achp.mapper.ChatSceneCategoryMapper;
import org.dominate.achp.service.IChatSceneCategoryService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 对话场景分类 服务实现类
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Service
public class ChatSceneCategoryServiceImpl extends ServiceImpl<ChatSceneCategoryMapper, ChatSceneCategory> implements IChatSceneCategoryService {

    @Override
    public List<SceneCategoryDTO> enabledList() {
        QueryWrapper<ChatSceneCategory> query = new QueryWrapper<>();
        query.lambda().eq(ChatSceneCategory::getDel, false).orderByAsc(ChatSceneCategory::getSeq);
        List<ChatSceneCategory> categoryList = list(query);
        return SceneWrapper.build().entityCategoryDTO(categoryList);
    }

    @Override
    public List<ChatSceneCategory> list(Collection<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        QueryWrapper<ChatSceneCategory> query = new QueryWrapper<>();
        query.lambda().in(ChatSceneCategory::getId, ids)
                .eq(ChatSceneCategory::getDel, false)
                .orderByAsc(ChatSceneCategory::getSeq);
        return list(query);
    }

    @Override
    public Map<Integer, List<SceneCategoryDTO>> map(Map<Integer, List<Integer>> groupCategoryIdMap) {
        if(CollectionUtils.isEmpty(groupCategoryIdMap)){
            return Collections.emptyMap();
        }
        Set<Integer> categoryIdSet = new HashSet<>();
        for (List<Integer> categoryIdList : groupCategoryIdMap.values()) {
            categoryIdSet.addAll(categoryIdList);
        }
        QueryWrapper<ChatSceneCategory> query = new QueryWrapper<>();
        query.lambda().in(ChatSceneCategory::getId, categoryIdSet)
                .eq(ChatSceneCategory::getDel, false);
        List<ChatSceneCategory> categoryList = list(query);
        Map<Integer, SceneCategoryDTO> categoryMap = new HashMap<>();
        for (ChatSceneCategory category : categoryList) {
            categoryMap.put(category.getId(), SceneWrapper.build().entityDTO(category));
        }
        Map<Integer, List<SceneCategoryDTO>> groupCategoryMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> groupCategoryIdEntry : groupCategoryIdMap.entrySet()) {
            List<SceneCategoryDTO> targetCategoryList = new ArrayList<>(groupCategoryIdEntry.getValue().size());
            for (Integer categoryId : groupCategoryIdEntry.getValue()) {
                targetCategoryList.add(categoryMap.get(categoryId));
            }
            groupCategoryMap.put(groupCategoryIdEntry.getKey(), targetCategoryList);
        }
        return groupCategoryMap;
    }

}
