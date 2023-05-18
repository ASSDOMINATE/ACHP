package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.UserDTO;
import org.dominate.achp.entity.dto.UserInfoDTO;
import org.dominate.achp.entity.req.IdReq;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.entity.wrapper.UserWrapper;
import org.dominate.achp.service.IUserAccountService;
import org.dominate.achp.service.IUserInfoService;
import org.dominate.achp.service.IUserRoleBindService;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/admin/user/")
@AllArgsConstructor
public class UserController {

    private final IUserInfoService userInfoService;
    private final IUserAccountService userAccountService;
    private final IUserRoleBindService userRoleBindService;


    @GetMapping(path = "list")
    @ResponseBody
    public Response<List<UserInfoDTO>> list(
            @RequestHeader(name = "token", required = false) String token,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);

        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().orderByDesc(UserInfo::getId)
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()));
        List<UserInfoDTO> userInfoList = UserWrapper.build().entityInfoDTO(userInfoService.list(query));
        setAdmin(userInfoList);
        return Response.data(userInfoList);
    }


    @GetMapping(path = "search")
    @ResponseBody
    public Response<List<UserInfoDTO>> list(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam String keyword
    ) {
        AuthHelper.checkAdminUser(token);
        List<UserInfoDTO> userList = userInfoService.search(keyword);
        setAdmin(userList);
        return Response.data(userList);
    }

    @PostMapping(path = "disableUser")
    @ResponseBody
    public Response<Boolean> disableUser(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody IdReq idReq
    ) {
        AuthHelper.checkAdminUser(token);
        userAccountService.updateState(idReq.getId(), UserState.DISABLED);
        QueryWrapper<UserInfo> userQuery = new QueryWrapper<>();
        userQuery.lambda().eq(UserInfo::getAccountId, idReq.getId());
        UserInfo info = new UserInfo();
        info.setState(UserState.DISABLED.getCode());
        userInfoService.update(info, userQuery);
        AuthHelper.setLogout(idReq.getId());
        return Response.success();
    }

    @GetMapping(path = "adminList")
    @ResponseBody
    public Response<List<UserInfoDTO>> adminList(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);
        if (StringUtil.isNotEmpty(keyword)) {
            List<UserInfoDTO> userList = userInfoService.search(keyword);
            setAdmin(userList);
            return Response.data(userList.stream().filter(UserDTO::getIsAdmin).collect(Collectors.toList()));
        }
        List<Integer> accountIdList = userRoleBindService.roleUserList(AuthHelper.ADMIN_ROLE_ID, page.getIndex(), page.getSize());
        QueryWrapper<UserInfo> query = new QueryWrapper<>();
        query.lambda().in(UserInfo::getAccountId, accountIdList);
        List<UserInfoDTO> userList = UserWrapper.build().entityInfoDTO(userInfoService.list(query));
        for (UserInfoDTO info : userList) {
            info.setIsAdmin(true);
        }
        return Response.data(userList);
    }


    private void setAdmin(List<UserInfoDTO> userList) {
        List<Integer> accountIdList = new ArrayList<>(userList.size());
        for (UserInfoDTO user : userList) {
            accountIdList.add(user.getAccountId());
        }
        List<Integer> adminIdList = userRoleBindService.hasRoleUserList(AuthHelper.ADMIN_ROLE_ID, accountIdList);
        for (UserInfoDTO user : userList) {
            user.setIsAdmin(adminIdList.contains(user.getAccountId()));
        }
    }

}
