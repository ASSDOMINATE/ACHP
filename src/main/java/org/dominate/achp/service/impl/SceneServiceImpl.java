package org.dominate.achp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.enums.SceneItemType;
import org.dominate.achp.entity.*;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.SceneCategoryDTO;
import org.dominate.achp.entity.dto.SceneDetailDTO;
import org.dominate.achp.entity.dto.SceneItemBaseDTO;
import org.dominate.achp.entity.req.SendSceneItemReq;
import org.dominate.achp.entity.req.SendSceneReq;
import org.dominate.achp.entity.wrapper.ChatWrapper;
import org.dominate.achp.entity.wrapper.SceneWrapper;
import org.dominate.achp.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景相关逻辑实现类
 *
 * @author dominate
 * @since 2023-04-14
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class SceneServiceImpl implements SceneService {

    private final IChatContentService chatContentService;

    private final IChatSceneService chatSceneService;
    private final IChatSceneItemService chatSceneItemService;
    private final IChatSceneConfService chatSceneConfService;
    private final IChatSceneRelateService chatSceneRelateService;
    private final IChatSceneCategoryService chatSceneCategoryService;

    @Override
    public SceneDetailDTO getSceneDetail(int sceneId) {
        ChatScene scene = chatSceneService.getById(sceneId);
        SceneDetailDTO detail = SceneWrapper.build().entityDetailDTO(scene);
        // content
        if (0 != scene.getContentId()) {
            ContentDTO content = ChatWrapper.build().entityDTO(chatContentService.getById(scene.getContentId()));
            detail.setContents(new ContentDTO[]{content});
        } else {
            detail.setContents(new ContentDTO[0]);
        }
        // item
        List<ChatSceneItem> itemList = chatSceneItemService.list(sceneId);
        List<SceneItemBaseDTO> items = new ArrayList<>(itemList.size());
        for (ChatSceneItem item : itemList) {
            SceneItemType itemType = SceneItemType.getValueByCode(item.getType());
            SceneItemBaseDTO baseItem = itemType.getItem().parseJson(item.getContent(), itemType, item.getTitle(), item.getId());
            items.add(baseItem);
        }
        detail.setItems(items.toArray(new SceneItemBaseDTO[0]));
        // category
        List<Integer> categoryIdList = chatSceneRelateService.getCategoryIdList(sceneId);
        List<ChatSceneCategory> categoryList = chatSceneCategoryService.list(categoryIdList);
        detail.setCategories(SceneWrapper.build().entityCategoryDTO(categoryList).toArray(new SceneCategoryDTO[0]));
        return detail;
    }

    @Override
    public String parseSceneContent(SendSceneReq sendScene) {
        Map<Integer, String> sendMap = new HashMap<>(sendScene.getItems().length);
        for (SendSceneItemReq item : sendScene.getItems()) {
            sendMap.put(item.getItemId(), item.getValue());
        }
        List<ChatSceneConf> confList = chatSceneConfService.list(sendScene.getSceneId());
        StringBuilder content = new StringBuilder();
        for (ChatSceneConf conf : confList) {
            // 排序数为0 或为展示类型 不进行拼接
            if (0 == conf.getSeq() || SceneItemType.isExplain(conf.getItemType())) {
                continue;
            }
            if (StringUtil.isNotEmpty(conf.getStart())) {
                content.append(conf.getStart());
            }
            int itemId = conf.getItemId();
            if (0 != itemId) {
                if (sendMap.containsKey(itemId)) {
                    content.append(sendMap.get(itemId));
                }
            }
            if (StringUtil.isNotEmpty(conf.getEnd())) {
                content.append(conf.getEnd());
            }
        }
        return content.toString();
    }

    @Override
    public boolean saveSceneRelate(int sceneId, List<Integer> categoryIdList, int accountId) {
        List<ChatSceneRelate> oldRelateIdList = chatSceneRelateService.getRelateList(sceneId, true);
        List<Integer> oldCategoryIdList = new ArrayList<>();
        for (ChatSceneRelate relate : oldRelateIdList) {
            oldCategoryIdList.add(relate.getCategoryId());
        }
        List<Integer> addCategoryIdList = new ArrayList<>(categoryIdList.size());
        for (Integer categoryId : categoryIdList) {
            if (!oldCategoryIdList.contains(categoryId)) {
                addCategoryIdList.add(categoryId);
            }
        }
        List<ChatSceneCategory> categoryList = chatSceneCategoryService.list(addCategoryIdList);
        List<ChatSceneRelate> insertList = new ArrayList<>(categoryList.size());
        for (ChatSceneCategory category : categoryList) {
            ChatSceneRelate relate = new ChatSceneRelate();
            relate.setSceneId(sceneId);
            relate.setCategoryId(category.getId());
            relate.setCategoryType(category.getType());
            relate.setCreateBy(accountId);
            relate.setUpdateBy(accountId);
            insertList.add(relate);
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            if (!chatSceneRelateService.saveBatch(insertList)) {
                return false;
            }
        }
        List<Integer> delCategoryIdList = new ArrayList<>(oldCategoryIdList.size());
        for (Integer categoryId : oldCategoryIdList) {
            if (!categoryIdList.contains(categoryId)) {
                delCategoryIdList.add(categoryId);
            }
        }
        if (CollectionUtils.isEmpty(delCategoryIdList)) {
            return true;
        }
        List<ChatSceneRelate> delRelateList = new ArrayList<>(delCategoryIdList.size());
        for (ChatSceneRelate relate : oldRelateIdList) {
            if (delCategoryIdList.contains(relate.getCategoryId())) {
                ChatSceneRelate delRelate = new ChatSceneRelate();
                delRelate.setId(relate.getId());
                delRelate.setDel(true);
                delRelate.setUpdateBy(accountId);
                delRelateList.add(delRelate);
            }
        }
        return chatSceneRelateService.updateBatchById(delRelateList);
    }

    @Override
    public boolean setSceneRelateFirst(int sceneId, int categoryId, int accountId) {
        QueryWrapper<ChatSceneRelate> query = new QueryWrapper<>();
        query.lambda().eq(ChatSceneRelate::getSceneId, sceneId)
                .eq(ChatSceneRelate::getCategoryId, categoryId);
        List<ChatSceneRelate> relateList = chatSceneRelateService.list(query);
        if (CollectionUtils.isEmpty(relateList)) {
            return false;
        }
        List<ChatSceneRelate> deleteList = new ArrayList<>(relateList.size());
        for (ChatSceneRelate relate : relateList) {
            ChatSceneRelate delRelate = new ChatSceneRelate();
            delRelate.setId(relate.getId());
            delRelate.setDel(true);
            delRelate.setUpdateBy(accountId);
            deleteList.add(delRelate);
        }
        if (!chatSceneRelateService.updateBatchById(deleteList)) {
            return false;
        }
        ChatSceneRelate insert = new ChatSceneRelate();
        insert.setSceneId(sceneId);
        insert.setCategoryId(categoryId);
        insert.setCategoryType(relateList.get(0).getCategoryType());
        insert.setCreateBy(relateList.get(0).getCreateBy());
        insert.setUpdateBy(accountId);
        return chatSceneRelateService.save(insert);
    }
}
