package org.dominate.achp.controller;

import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.ConfigCache;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.common.enums.ResponseType;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.dto.AppConfigDTO;
import org.dominate.achp.entity.dto.AppNoticeDTO;
import org.dominate.achp.entity.dto.UserAuthDTO;
import org.dominate.achp.entity.req.InfoReq;
import org.dominate.achp.entity.req.ModifyPasswordReq;
import org.dominate.achp.entity.req.SetPasswordReq;
import org.dominate.achp.entity.req.ValidMobileReq;
import org.dominate.achp.service.IUserAccountService;
import org.dominate.achp.service.IUserInfoService;
import org.dominate.achp.sys.Response;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户相关接口
 *
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class ApiUserController {

    private final IUserAccountService userAccountService;
    private final IUserInfoService userInfoService;

    @GetMapping(path = "notice")
    @ResponseBody
    public Response<AppNoticeDTO> notice() {
        return Response.data(ConfigCache.getAppNotice());
    }

    @GetMapping(path = "config")
    @ResponseBody
    public Response<AppConfigDTO> config(
            @RequestParam(name = "version", required = false) String version,
            @RequestParam(name = "platform", required = false) String platform
    ) {
        return Response.data(ConfigCache.getAppConfig(version, platform));
    }

    @GetMapping(path = "sendValid")
    @ResponseBody
    public Response<Boolean> sendValid(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam(name = "mobile", required = false) String mobile
    ) {
        //TODO 对用户发送短信做下记录
        if (StringUtil.isEmpty(token)) {
            if (StringUtil.isEmpty(mobile)) {
                throw BusinessException.create(ExceptionType.PARAM_ERROR);
            }
            AuthHelper.setMobileValid(mobile);
            return Response.success();
        }
        UserAuthDTO userAuth = AuthHelper.parseWithValid(token);
        if (StringUtil.isNotEmpty(mobile)) {
            AuthHelper.setMobileValid(mobile);
            return Response.success();
        }
        if (StringUtil.isEmpty(userAuth.getPhone())) {
            throw BusinessException.create(ExceptionType.NOT_BIND_PHONE);
        }
        AuthHelper.setMobileValid(userAuth.getPhone());
        return Response.success();

    }

    @PostMapping(path = "modifyPwd")
    @ResponseBody
    public Response<Boolean> modifyPwd(
            @RequestHeader String token,
            @Validated @RequestBody ModifyPasswordReq modifyPasswordReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        boolean modifySuccess = userAccountService.modifyPassword(accountId,
                modifyPasswordReq.getOldPwd(), modifyPasswordReq.getNewPwd());
        return Response.data(modifySuccess);
    }

    @PostMapping(path = "setPwd")
    @ResponseBody
    public Response<Boolean> setPassword(
            @RequestHeader String token,
            @Validated @RequestBody SetPasswordReq setPasswordReq
    ) {
        UserAuthDTO userAuth = AuthHelper.parseWithValid(token);
        if (!AuthHelper.checkMobileValid(userAuth.getPhone(), setPasswordReq.getCode())) {
            return Response.code(ResponseType.MOBILE_VALID_CODE_ERROR);
        }
        return Response.data(userAccountService.setPassword(userAuth.getAccountId(), setPasswordReq.getPwd()));
    }

    @PostMapping(path = "bindPhone")
    @ResponseBody
    public Response<Boolean> bindPhone(
            @RequestHeader String token,
            @Validated @RequestBody ValidMobileReq validMobileReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        if (!AuthHelper.checkMobileValid(validMobileReq.getMobile(), validMobileReq.getCode())) {
            return Response.code(ResponseType.MOBILE_VALID_CODE_ERROR);
        }
        InfoReq info = new InfoReq();
        info.setAccountId(accountId);
        info.setPhone(validMobileReq.getMobile());
        return Response.data(userInfoService.saveInfo(info));
    }

    @PostMapping(path = "setInfo")
    @ResponseBody
    public Response<Boolean> setInfo(
            @RequestHeader String token,
            @Validated @RequestBody InfoReq infoReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        infoReq.setAccountId(accountId);
        return Response.data(userInfoService.saveInfo(infoReq));
    }


}
