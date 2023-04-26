package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.RecommendCache;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.ChatScene;
import org.dominate.achp.entity.ChatSceneCategory;
import org.dominate.achp.entity.ChatSceneConf;
import org.dominate.achp.entity.ChatSceneItem;
import org.dominate.achp.entity.dto.SceneInfoDTO;
import org.dominate.achp.entity.req.*;
import org.dominate.achp.entity.wrapper.SceneWrapper;
import org.dominate.achp.service.*;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        saveScene.setDesr(infoReq.getDesr());
        saveScene.setNotice(infoReq.getNotice());
        saveScene.setDel(infoReq.getDel());
        saveScene.setUpdateBy(accountId);
        saveScene.setImgSrc(infoReq.getImgSrc());
        saveScene.setSetSystem(infoReq.getSystem());
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
        AuthHelper.checkAdminUser(token);
        RecommendCache.addSceneIdList(idReq.getId());
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
        // 不会数量很多，暂先用循环添加
        // TODO 可优化
        for (int i = 0; i < infoReq.getItems().length; i++) {
            ChatSceneItem item = infoReq.getItems()[i];
            item.setSceneId(infoReq.getId());
            item.setCreateBy(accountId);
            item.setUpdateBy(accountId);
            chatSceneItemService.save(item);
            ChatSceneConf config = infoReq.getConfigs()[i];
            config.setSceneId(infoReq.getId());
            config.setItemId(item.getId());
            config.setCreateBy(accountId);
            config.setUpdateBy(accountId);
            chatSceneConfService.save(config);
        }
    }


}
