package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.RecommendCache;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.*;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.SceneInfoDTO;
import org.dominate.achp.entity.req.*;
import org.dominate.achp.entity.wrapper.ChatWrapper;
import org.dominate.achp.entity.wrapper.SceneWrapper;
import org.dominate.achp.service.*;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/admin/scene/")
@AllArgsConstructor
public class SceneController {

    private final IChatSceneCategoryService chatSceneCategoryService;
    private final IChatSceneService chatSceneService;
    private final IChatSceneRelateService chatSceneRelateService;
    private final IChatSceneItemService chatSceneItemService;
    private final IChatSceneConfService chatSceneConfService;
    private final IChatContentService chatContentService;

    private final SceneService sceneService;

    @GetMapping(path = "list")
    @ResponseBody
    public Response<List<ChatScene>> sceneList(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "del", required = false) Boolean del,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);
        QueryWrapper<ChatScene> query = new QueryWrapper<>();
        query.lambda().eq(StringUtil.isNotEmpty(title), ChatScene::getTitle, title)
                .eq(null != del, ChatScene::getDel, del)
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()));
        List<ChatScene> list = chatSceneService.list(query);
        return Response.data(list);
    }

    @GetMapping(path = "info")
    @ResponseBody
    public Response<SceneInfoDTO> info(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam Integer id
    ) {
        AuthHelper.checkAdminUser(token);
        ChatScene scene = chatSceneService.getById(id);
        SceneInfoDTO info = SceneWrapper.build().entityInfoDTO(scene);
        List<ChatSceneItem> itemList = chatSceneItemService.list(info.getId());
        List<ChatSceneConf> confList = chatSceneConfService.list(info.getId());
        List<Integer> categoryIdList = chatSceneRelateService.getCategoryIdList(info.getId());
        List<ChatSceneCategory> categoryList = chatSceneCategoryService.list(categoryIdList);
        if (0 != scene.getContentId()) {
            ContentDTO content = ChatWrapper.build().entityDTO(chatContentService.getById(scene.getContentId()));
            info.setContents(new ContentDTO[]{content});
        } else {
            info.setContents(new ContentDTO[0]);
        }
        info.setItems(itemList.toArray(new ChatSceneItem[0]));
        info.setConfigs(confList.toArray(new ChatSceneConf[0]));
        info.setCategories(categoryList.toArray(new ChatSceneCategory[0]));
        return Response.data(info);
    }

    @PostMapping(path = "saveInfo")
    @ResponseBody
    public Response<Boolean> saveInfo(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated SceneInfoReq infoReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        ChatScene saveScene = new ChatScene();
        saveScene.setTitle(infoReq.getTitle());
        saveScene.setSeq(infoReq.getSeq());
        saveScene.setDesr(infoReq.getDesr());
        saveScene.setNotice(infoReq.getNotice());
        saveScene.setDel(infoReq.getDel());
        saveScene.setUpdateBy(accountId);
        saveScene.setImgSrc(infoReq.getImgSrc());
        saveScene.setSetSystem(infoReq.getSystem());
        int contentId = 0;
        if (StringUtil.isNotEmpty(infoReq.getReply()) || StringUtil.isNotEmpty(infoReq.getSentence())) {
            contentId = saveContent(infoReq.getSentence(), infoReq.getReply());
        }
        saveScene.setContentId(contentId);
        if (null == infoReq.getId()) {
            saveScene.setCreateBy(accountId);
            if (!chatSceneService.save(saveScene)) {
                return Response.failed();
            }
            infoReq.setId(saveScene.getId());
            if (!sceneService.saveSceneRelate(infoReq.getId(), Arrays.asList(infoReq.getCategoryIds()), accountId)) {
                return Response.failed();
            }
            saveItemAndConfig(infoReq, accountId);
            return Response.success();
        }
        saveScene.setId(infoReq.getId());
        if (!chatSceneService.updateById(saveScene)) {
            return Response.failed();
        }
        if (!sceneService.saveSceneRelate(saveScene.getId(), Arrays.asList(infoReq.getCategoryIds()), accountId)) {
            return Response.failed();
        }
        setItemAndConfigDel(infoReq.getId(), accountId);
        saveItemAndConfig(infoReq, accountId);
        return Response.success();
    }

    @PostMapping(path = "setCategoryFirst")
    @ResponseBody
    public Response<Boolean> setCategoryFirst(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated SetRelateReq setCategoryReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        return Response.data(sceneService.setSceneRelateFirst(setCategoryReq.getSceneId(), setCategoryReq.getCategoryId(), accountId));
    }


    @PostMapping(path = "setRecommend")
    @ResponseBody
    public Response<Boolean> setRecommend(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated IdsReq idsReq
    ) {
        AuthHelper.checkAdminUser(token);
        RecommendCache.updateSceneIdList(idsReq.getIds());
        return Response.success();
    }

    @PostMapping(path = "addRecommend")
    @ResponseBody
    public Response<Boolean> setRecommend(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated IdReq idReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        RecommendCache.addSceneIdList(idReq.getId());
        ChatScene update = new ChatScene();
        update.setId(idReq.getId());
        update.setForRecommend(true);
        update.setUpdateBy(accountId);
        chatSceneService.updateById(update);
        return Response.success();
    }

    @PostMapping(path = "removeRecommend")
    @ResponseBody
    public Response<Boolean> removeRecommend(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated IdReq idReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        List<Integer> idList = RecommendCache.getSceneIdList();
        idList.remove(idReq.getId());
        RecommendCache.updateSceneIdList(idList);
        ChatScene update = new ChatScene();
        update.setId(idReq.getId());
        update.setForRecommend(false);
        update.setUpdateBy(accountId);
        chatSceneService.updateById(update);
        return Response.success();
    }


    @GetMapping(path = "category")
    @ResponseBody
    public Response<List<ChatSceneCategory>> categoryList(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "del", required = false) Boolean del,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);
        QueryWrapper<ChatSceneCategory> query = new QueryWrapper<>();
        query.lambda().eq(StringUtil.isNotEmpty(name), ChatSceneCategory::getName, name)
                .eq(null != type, ChatSceneCategory::getType, type)
                .eq(null != del, ChatSceneCategory::getDel, del)
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()));
        List<ChatSceneCategory> list = chatSceneCategoryService.list(query);
        return Response.data(list);
    }

    @PostMapping(path = "saveCategory")
    @ResponseBody
    public Response<Boolean> saveCategory(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated SceneCategoryReq categoryReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        ChatSceneCategory save = new ChatSceneCategory();
        save.setSeq(categoryReq.getSeq());
        save.setType(categoryReq.getType());
        save.setName(categoryReq.getName());
        save.setDesr(categoryReq.getDesr());
        save.setDel(categoryReq.getDel());
        save.setUpdateBy(accountId);
        if (null == categoryReq.getId()) {
            save.setCreateBy(accountId);
            return Response.data(chatSceneCategoryService.save(save));
        }
        save.setId(categoryReq.getId());
        return Response.data(chatSceneCategoryService.updateById(save));
    }

    private int saveContent(String sentence, String reply) {
        ChatContent content = new ChatContent();
        content.setReply(reply);
        content.setSentence(sentence);
        content.setModelId(StringUtil.EMPTY);
        content.setLastId(0);
        chatContentService.save(content);
        return content.getId();
    }

    private void setItemAndConfigDel(int sceneId, int accountId) {
        ChatSceneItem item = new ChatSceneItem();
        item.setUpdateBy(accountId);
        item.setDel(true);
        QueryWrapper<ChatSceneItem> itemQuery = new QueryWrapper<>();
        itemQuery.lambda().eq(ChatSceneItem::getSceneId, sceneId)
                .eq(ChatSceneItem::getDel, false);
        chatSceneItemService.update(item, itemQuery);

        ChatSceneConf config = new ChatSceneConf();
        config.setUpdateBy(accountId);
        config.setDel(true);
        QueryWrapper<ChatSceneConf> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(ChatSceneConf::getSceneId, sceneId)
                .eq(ChatSceneConf::getDel, false);
        chatSceneConfService.update(config, configQuery);
    }

    private void saveItemAndConfig(SceneInfoReq infoReq, int accountId) {
        List<ChatSceneConf> configSaveList = new ArrayList<>(infoReq.getConfigs().length);
        for (int i = 0; i < infoReq.getItems().length; i++) {
            ChatSceneItem item = infoReq.getItems()[i];
            item.setSceneId(infoReq.getId());
            item.setCreateBy(accountId);
            item.setUpdateBy(accountId);
            // 因为需要返回值 item id
            chatSceneItemService.save(item);

            ChatSceneConf config = infoReq.getConfigs()[i];
            config.setSceneId(infoReq.getId());
            config.setCreateBy(accountId);
            config.setUpdateBy(accountId);
            config.setItemType(item.getType());
            config.setItemId(item.getId());
            configSaveList.add(config);
        }
        chatSceneConfService.saveBatch(configSaveList);
    }


}
