package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hwja.tool.utils.RandomUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.cache.PermissionCache;
import org.dominate.achp.common.enums.PlatformClientType;
import org.dominate.achp.common.enums.ResponseType;
import org.dominate.achp.common.enums.UserState;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.UserInfo;
import org.dominate.achp.entity.dto.InitAccountDTO;
import org.dominate.achp.entity.dto.UserAuthDTO;
import org.dominate.achp.entity.req.*;
import org.dominate.achp.service.*;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    private final IUserRoleBindService userRoleBindService;


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
        if (null == loginReq.getSkipPerm()) {
            loginReq.setSkipPerm(true);
        }
        PlatformClientType platform = PlatformClientType.getValueByCode(loginReq.getPlatform());
        return loginAccount(accountId, loginReq.getPwd(), platform.getId(), loginReq.getSkipPerm());
    }

    @PostMapping(path = "loginCode")
    @ResponseBody
    public Response<String> loginCode(
            @Validated @RequestBody LoginCodeReq LoginCodeReq
    ) {
        if (!AuthHelper.checkMobileValid(LoginCodeReq.getMobile(), LoginCodeReq.getCode())) {
            return Response.code(ResponseType.MOBILE_VALID_CODE_ERROR);
        }
        int accountId = userInfoService.find(LoginCodeReq.getMobile());
        return loginAccount(accountId);
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
        String defaultPassword = RandomUtil.genRandomNum(6).toLowerCase();
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
        userAccountService.updateState(accountId, setState);
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
        if (!PermissionCache.hasPerm(path, userAuth.getPermissions())) {
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

    @PostMapping(path = "setAdmin")
    @ResponseBody
    public Response<Boolean> setAdmin(
            @RequestHeader String token,
            @RequestBody @Validated SetAdminReq setAdminReq
    ) {
        AuthHelper.checkAdminUser(token);
        if (setAdminReq.getIsAdd()) {
            userRoleBindService.add(setAdminReq.getAccountId(), AuthHelper.ADMIN_PERMISSION_ID);
        } else {
            userRoleBindService.delete(setAdminReq.getAccountId(), AuthHelper.ADMIN_PERMISSION_ID);
        }
        return Response.success();
    }

    @PostMapping(path = "updateToken")
    @ResponseBody
    public Response<String> updateToken(
            @RequestHeader String token
    ) {
        UserAuthDTO auth = AuthHelper.parseWithValid(token);
        if (null == auth) {
            return Response.code(ResponseType.IDENTITY_STATE_WRONG);
        }
        UserInfo info = userInfoService.getInfo(auth.getAccountId());
        return Response.data(AuthHelper.setAuth(info, auth.getPlatformId(), Collections.emptyList()));
    }


    private Response<String> loginAccount(int accountId, String password, int platformId, boolean skipPerm) {
        return loginAccount(accountId, password, true, platformId, skipPerm);
    }

    private Response<String> loginAccount(int accountId) {
        return loginAccount(accountId, StringUtil.EMPTY, false, PlatformClientType.APP.getId(), true);
    }

    /**
     * @param accountId  账号ID
     * @param password   密码
     * @param needValid  是否验证密码
     * @param platformId 登陆平台ID
     * @param skipPerm   是否跳过权限
     * @return JwtToken
     */
    private Response<String> loginAccount(int accountId, String password, boolean needValid, int platformId, boolean skipPerm) {
        if (0 == accountId) {
            return Response.code(ResponseType.LOGIN_SIGN_NOT_FOUND);
        }
        // 校验密码未通过
        if (needValid && !userAccountService.validPassword(accountId, password)) {
            return Response.code(ResponseType.WRONG_PASSWORD);
        }
        UserInfo info = userInfoService.getInfo(accountId);
        // 是否校验平台权限
        if (skipPerm) {
            return Response.data(AuthHelper.setAuth(info, platformId, Collections.emptyList()));
        }
        List<Integer> permissionList = dataService.getUserPermissionIdList(info.getAccountId(), platformId);
        if (CollectionUtils.isEmpty(permissionList)) {
            return Response.code(ResponseType.NO_PERMISSION);
        }
        return Response.data(AuthHelper.setAuth(info, platformId, permissionList));
    }
}
