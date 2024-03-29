package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.theokanning.openai.model.Model;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.RecommendCache;
import org.dominate.achp.common.cache.StatisticCache;
import org.dominate.achp.common.enums.SceneCountType;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.entity.dto.SceneCategoryDTO;
import org.dominate.achp.entity.dto.SceneDTO;
import org.dominate.achp.entity.dto.SceneDetailDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.entity.req.SendSceneReq;
import org.dominate.achp.service.*;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 场景相关接口
 *
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/scene")
@AllArgsConstructor
public class ApiSceneController {

    private final SceneService sceneService;

    private final IChatRecordService chatRecordService;
    private final IChatSceneService chatSceneService;
    private final IChatSceneRelateService chatSceneRelateService;
    private final IChatSceneCategoryService chatSceneCategoryService;


    /**
     * 获取场景分类列表
     *
     * @return 场景分类列表
     */
    @GetMapping(path = "getCategory")
    @ResponseBody
    public Response<List<SceneCategoryDTO>> getCategory() {
        List<SceneCategoryDTO> categoryList = chatSceneCategoryService.enabledList();
        return Response.data(categoryList);
    }

    /**
     * 获取场景列表
     *
     * @param pageReq 分页参数
     * @return 场景列表
     */
    @GetMapping(path = "getSceneList")
    @ResponseBody
    public Response<List<SceneDTO>> getSceneList(
            @RequestParam(name = "category_id", required = false, defaultValue = "0") Integer categoryId,
            @Validated PageReq pageReq
    ) {
        if (0 == categoryId) {
            List<SceneDTO> sceneList = chatSceneService.list(pageReq);
            fillCategory(sceneList);
            return Response.data(sceneList);
        }
        List<Integer> sceneIdList = chatSceneRelateService.getSceneIdList(categoryId, pageReq);
        List<SceneDTO> sceneList = chatSceneService.list(sceneIdList);
        fillCategory(sceneList, sceneIdList);
        return Response.data(sorted(sceneIdList, sceneList));
    }


    /**
     * 获取用户场景列表
     *
     * @param token   用户标识
     * @param pageReq 分页参数
     * @return 场景列表
     */
    @GetMapping(path = "getUserSceneList")
    @ResponseBody
    public Response<List<SceneDTO>> getUserSceneList(
            @RequestHeader(name = "token", required = false) String token,
            @Validated PageReq pageReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        List<Integer> sceneIdList = chatRecordService.getUserSceneIdList(accountId, pageReq);
        List<SceneDTO> sceneList = chatSceneService.list(sceneIdList);
        fillCategory(sceneList, sceneIdList);
        return Response.data(sorted(sceneIdList, sceneList));
    }

    /**
     * 获取推荐场景列表
     *
     * @return 场景列表
     */
    @GetMapping(path = "getRecommendSceneList")
    @ResponseBody
    public Response<List<SceneDTO>> getRecommendSceneList() {
        List<Integer> sceneIdList = RecommendCache.getSceneIdList();
        List<SceneDTO> sceneList = chatSceneService.list(sceneIdList);
        fillCategory(sceneList, sceneIdList);
        return Response.data(sorted(sceneIdList, sceneList));
    }


    /**
     * 获取场景详情
     *
     * @param id 场景ID
     * @return 场景详情
     */
    @GetMapping(path = "getSceneDetail")
    @ResponseBody
    public Response<SceneDetailDTO> getSceneDetail(
            @RequestParam(name = "id") Integer id
    ) {
        SceneDetailDTO detail = sceneService.getSceneDetail(id);
        StatisticCache.addSceneCount(id, SceneCountType.READ);
        return Response.data(detail);
    }

    /**
     * 解析场景发送信息
     *
     * @param sendSceneReq 场景发送参数
     * @return 场景发送信息
     */
    @PostMapping(path = "parseSceneSend")
    @ResponseBody
    public Response<String> parseSceneSend(
            @Validated @RequestBody SendSceneReq sendSceneReq
    ) {
        String send = sceneService.parseSceneContent(sendSceneReq);
        StatisticCache.addSceneCount(sendSceneReq.getSceneId(), SceneCountType.SEND);
        return Response.data(send);
    }

    @GetMapping(path = "models")
    @ResponseBody
    public Response<List<Model>> modelList() {
        return Response.data(ChatGptHelper.modelList());
    }

    private static List<SceneDTO> sorted(List<Integer> sceneIdList, List<SceneDTO> sceneList) {
        Map<Integer, SceneDTO> sceneMap = new HashMap<>(sceneList.size());
        for (SceneDTO scene : sceneList) {
            sceneMap.put(scene.getId(), scene);
        }
        List<SceneDTO> target = new ArrayList<>(sceneList.size());
        for (Integer id : sceneIdList) {
            if (!sceneMap.containsKey(id)) {
                continue;
            }
            target.add(sceneMap.get(id));
        }
        return target;
    }

    private void fillCategory(List<SceneDTO> sceneList) {
        fillCategory(sceneList, Collections.emptyList());
    }

    private void fillCategory(List<SceneDTO> sceneList, List<Integer> sceneIdList) {
        // TODO 暂时不启用
        if (true) {
            return;
        }
        if (CollectionUtils.isEmpty(sceneIdList)) {
            sceneIdList = new ArrayList<>();
            for (SceneDTO scene : sceneList) {
                sceneIdList.add(scene.getId());
            }
        }
        Map<Integer, List<Integer>> sceneCategoryIdMap = chatSceneRelateService.getCategoryIdMap(sceneIdList);
        Map<Integer, List<SceneCategoryDTO>> sceneCategoryMap = chatSceneCategoryService.map(sceneCategoryIdMap);
        for (SceneDTO scene : sceneList) {
            if (!sceneCategoryMap.containsKey(scene.getId())) {
                scene.setCategories(new SceneCategoryDTO[0]);
                continue;
            }
            scene.setCategories(sceneCategoryMap.get(scene.getId()).toArray(new SceneCategoryDTO[0]));
        }
    }
}
