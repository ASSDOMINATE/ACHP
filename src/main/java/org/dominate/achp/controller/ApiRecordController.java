package org.dominate.achp.controller;

import lombok.AllArgsConstructor;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.common.helper.WeChatPayHelper;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.GroupDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.service.IChatContentService;
import org.dominate.achp.service.IChatGroupService;
import org.dominate.achp.service.IChatRecordService;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对话记录相关接口
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/record")
@AllArgsConstructor
public class ApiRecordController {

    private final IChatRecordService chatRecordService;
    private final IChatGroupService chatGroupService;
    private final IChatContentService chatContentService;

    /**
     * 获取用户对话组列表
     *
     * @param token   用户标识
     * @param pageReq 分页参数
     * @return 对话组列表
     */
    @GetMapping(path = "getUserGroupList")
    @ResponseBody
    public Response<List<GroupDTO>> getUserGroupList(
            @RequestHeader(name = "token", required = false) String token,
            @Validated PageReq pageReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        List<String> groupIdList = chatRecordService.getUserGroupIdList(accountId, pageReq);
        List<GroupDTO> groupList = chatGroupService.list(groupIdList);
        return Response.data(groupList);
    }

    /**
     * 获取对话内容列表
     *
     * @param id      对话组ID
     * @param pageReq 分页参数
     * @return 对话内容列表
     */
    @GetMapping(path = "getContentList")
    @ResponseBody
    public Response<List<ContentDTO>> getContentList(
            @RequestParam(name = "id") String id,
            @Validated PageReq pageReq
    ) {
        List<Integer> contentIdList = chatRecordService.getGroupContentIdList(id, pageReq);
        List<ContentDTO> contentList = chatContentService.list(contentIdList);
        return Response.data(contentList);
    }


}
