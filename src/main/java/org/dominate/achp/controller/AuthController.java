package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.enums.ResponseType;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.common.helper.PermissionHelper;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.InitAccountDTO;
import org.dominate.achp.entity.dto.UserAuthDTO;
import org.dominate.achp.entity.req.InfoReq;
import org.dominate.achp.entity.req.InitAccountReq;
import org.dominate.achp.entity.req.LoginReq;
import org.dominate.achp.entity.req.RegisterReq;
import org.dominate.achp.service.DataService;
import org.dominate.achp.service.IUserAccountService;
import org.dominate.achp.service.IUserInfoService;
import org.dominate.achp.service.IUserPermissionService;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证相关接口
 *
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final DataService dataService;

    private final IUserAccountService userAccountService;
    private final IUserInfoService userInfoService;
    private final IUserPermissionService userPermissionService;


    /**
     * 登录
     * 1.查询用户
     * 2.验证用户
     * 3.设置登录
     * 4.返回JwtToken
     *
     * @param loginReq 手机号/邮箱/唯一编码 + 密码
     * @return JwtToken
     */
    @PostMapping(path = "login")
    @ResponseBody
    public Response<String> login(
            @Validated @RequestBody LoginReq loginReq
    ) {
        int accountId = userInfoService.find(loginReq.getSign());
        if (null == loginReq.getForClient()) {
            loginReq.setForClient(true);
        }
        return loginAccount(accountId, loginReq.getPwd(), 0, loginReq.getForClient());
    }

    @PostMapping(path = "register")
    @ResponseBody
    public Response<String> register(
            @Validated @RequestBody RegisterReq registerReq
    ) {
        int accountId = userAccountService.addAccount(registerReq.getPwd());
        InfoReq info = InfoReq.registerInfo(accountId, registerReq.getSign());
        if (userInfoService.saveInfo(info)) {
            if (userAccountService.updateState(accountId, UserState.NORMAL)) {
                return loginAccount(accountId, registerReq.getPwd(), 0, true);
            }
        }
        return Response.failed();
    }

    /**
     * 初始化账号
     *
     * @param initAccountReq 尽可能唯一的设备标记
     * @return JwtToken
     */
    @PostMapping(path = "initAccount")
    @ResponseBody
    public Response<InitAccountDTO> initAccount(
            @Validated @RequestBody InitAccountReq initAccountReq
    ) {
        // 自动生成账号的初始密码
        String defaultPassword = initAccountReq.getDeviceSign().substring(0, 6);
        int accountId = userAccountService.addAccount(defaultPassword);
        InfoReq info = InfoReq.initInfo(accountId, initAccountReq.getDeviceSign());
        if (userInfoService.saveInfo(info)) {
            if (userAccountService.updateState(accountId, UserState.NORMAL)) {
                String jwtToken = loginAccount(accountId, defaultPassword, 0, true).getData();
                InitAccountDTO initAccount = new InitAccountDTO();
                initAccount.setPassword(defaultPassword);
                initAccount.setJwtToken(jwtToken);
                return Response.data(initAccount);
            }
        }
        return Response.failed();
    }


    /**
     * 登出
     * 使 JwtToken 失效
     *
     * @param token JwtToken
     * @return 固定返回成功
     */
    @GetMapping(path = "logout")
    @ResponseBody
    public Response<String> logout(
            @RequestHeader String token
    ) {
        try {
            AuthHelper.setInvalid(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.success();
    }

    @PostMapping(path = "logOff")
    @ResponseBody
    public Response<Boolean> logOff(
            @RequestHeader String token
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        UserState setState = UserState.DISABLED;
        userAccountService.updateState(accountId,setState);
        InfoReq info = new InfoReq();
        info.setAccountId(accountId);
        info.setState(setState.getCode());
        return Response.data(userInfoService.saveInfo(info));
    }

    /**
     * 解析 JwtToken 格式+状态检查
     * 返回解析后的用户信息
     *
     * @param token JwtToken
     * @return 用户授权信息
     */
    @GetMapping(path = "user")
    @ResponseBody
    public Response<UserAuthDTO> parseUser(
            @RequestHeader String token
    ) {
        UserAuthDTO userAuth = AuthHelper.parseWithValid(token);
        if (null == userAuth) {
            return Response.code(ResponseType.IDENTITY_STATE_WRONG);
        }
        return Response.data(userAuth);
    }


    // 后台服务

    /**
     * 验证 JwtToken 格式+状态检查
     * path不为空时还会进行权限效验
     *
     * @param token JwtToken
     * @param path  权限效验路径
     * @return 是否有效/有权限
     */
    @GetMapping(path = "verify")
    @ResponseBody
    public Response<Boolean> verifyToken(
            @RequestHeader(name = "token") String token,
            @RequestParam(required = false) String path
    ) {
        UserAuthDTO userAuth;
        try {
            userAuth = AuthHelper.parse(token);
        } catch (Exception e) {
            return Response.code(ResponseType.INVALID_TOKEN);
        }
        if (!AuthHelper.isValid(userAuth)) {
            return Response.code(ResponseType.IDENTITY_STATE_WRONG);
        }
        if (StringUtils.isEmpty(path)) {
            return Response.data(true);
        }
        if (!PermissionHelper.hasPerm(path, userAuth.getPermissions())) {
            return Response.code(ResponseType.NO_PERMISSION);
        }
        return Response.data(true);
    }

    /**
     * 解析Token菜单路径
     *
     * @param token JwtToken
     * @return 菜单路径列表
     */
    @GetMapping(path = "menu")
    @ResponseBody
    public Response<List<String>> parseMenu(
            @RequestHeader String token
    ) {
        UserAuthDTO userAuth;
        try {
            userAuth = AuthHelper.parse(token);
        } catch (Exception e) {
            return Response.code(ResponseType.INVALID_TOKEN);
        }
        return Response.data(userPermissionService.parsePathList(userAuth.getPermissions(), 0));
    }


    private Response<String> loginAccount(int accountId, String password, int platformId, boolean forClient) {
        return loginAccount(accountId, password, true, platformId, forClient);
    }

    private Response<String> loginAccount(int accountId, String password, boolean needValid, int platformId, boolean forClient) {
        if (0 == accountId) {
            return Response.code(ResponseType.LOGIN_SIGN_NOT_FOUND);
        }
        if (needValid && !userAccountService.validPassword(accountId, password)) {
            return Response.code(ResponseType.WRONG_PASSWORD);
        }
        UserInfo info = userInfoService.getInfo(accountId);
        List<Integer> permissionList = dataService.getUserPermissionIdList(accountId, platformId);
        if (0 != platformId && CollectionUtils.isEmpty(permissionList) && !forClient) {
            return Response.code(ResponseType.NO_PERMISSION);
        }
        String token = AuthHelper.setAuth(info, platformId, permissionList);
        return Response.data(token);
    }
}
