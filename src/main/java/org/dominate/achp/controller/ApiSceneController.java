package org.dominate.achp.controller;

import com.theokanning.openai.model.Model;
import lombok.AllArgsConstructor;
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

import java.util.List;

/**
 * 场景相关接口
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/scene")
@AllArgsConstructor
public class ApiSceneController {

    private final ChatService chatService;
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
            return Response.data(sceneList);
        }
        List<Integer> sceneIdList = chatSceneRelateService.getSceneIdList(categoryId,pageReq);
        List<SceneDTO> sceneList = chatSceneService.list(sceneIdList);
        return Response.data(sceneList);
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
        return Response.data(sceneList);
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
        return Response.data(send);
    }

    @GetMapping(path = "models")
    @ResponseBody
    public Response<List<Model>> modelList() {
        return Response.data(ChatGptHelper.modelList());
    }

}
