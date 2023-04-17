package org.dominate.achp.controller;

import lombok.AllArgsConstructor;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.UserDTO;
import org.dominate.achp.entity.req.IdReq;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.service.IUserAccountService;
import org.dominate.achp.service.IUserInfoService;
import org.dominate.achp.service.IUserRoleBindService;
import org.dominate.achp.service.IUserRoleService;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public Response<List<UserDTO>> list(
            @RequestHeader(name = "token", required = false) String token,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);
        List<UserDTO> userList = userInfoService.getDTOList(page.getIndex(), page.getSize(), false);
        setAdmin(userList);
        return Response.data(userList);
    }


    @GetMapping(path = "search")
    @ResponseBody
    public Response<List<UserDTO>> list(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam String keyword

    ) {
        AuthHelper.checkAdminUser(token);
        List<UserDTO> userList = userInfoService.search(keyword);
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
        if (!userAccountService.updateState(idReq.getId(), UserState.DISABLED)) {
            UserInfo info = new UserInfo();
            info.setId(idReq.getId());
            info.setState(UserState.DISABLED.getCode());
            userInfoService.updateById(info);
            return Response.failed();
        }
        AuthHelper.setLogout(idReq.getId());
        return Response.success();
    }

    private void setAdmin(List<UserDTO> userList) {
        List<Integer> accountIdList = new ArrayList<>(userList.size());
        for (UserDTO user : userList) {
            accountIdList.add(user.getAccountId());
        }
        List<Integer> adminIdList = userRoleBindService.hasRoleUserList(AuthHelper.ADMIN_ROLE_ID, accountIdList);
        for (UserDTO user : userList) {
            user.setIsAdmin(adminIdList.contains(user.getAccountId()));
        }
    }


}
