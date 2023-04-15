package org.dominate.achp.controller;

import lombok.AllArgsConstructor;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.dto.UserDTO;
import org.dominate.achp.entity.req.IdReq;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.service.IUserAccountService;
import org.dominate.achp.service.IUserInfoService;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping(path = "list")
    @ResponseBody
    public Response<List<UserDTO>> list(
            @RequestHeader(name = "token", required = false) String token,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);
        List<UserDTO> userList = userInfoService.getDTOList(page.getIndex(), page.getSize(), false);
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
            return Response.failed();
        }
        AuthHelper.setLogout(idReq.getId());
        return Response.success();
    }


}
