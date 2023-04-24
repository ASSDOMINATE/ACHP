package org.dominate.achp.controller;

import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.CardCache;
import org.dominate.achp.common.cache.ConfigCache;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.BaseKey;
import org.dominate.achp.entity.dto.AppConfigDTO;
import org.dominate.achp.entity.dto.AppNoticeDTO;
import org.dominate.achp.entity.req.ConfigReq;
import org.dominate.achp.entity.req.KeyReq;
import org.dominate.achp.service.IBaseConfigService;
import org.dominate.achp.service.IBaseKeyService;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/admin/base/")
@AllArgsConstructor
public class BaseController {

    private final IBaseConfigService baseConfigService;
    private final IBaseKeyService baseKeyService;


    @GetMapping(path = "config")
    @ResponseBody
    public Response<BaseConfig> config(
            @RequestHeader(name = "token", required = false) String token
    ) {
        AuthHelper.checkAdminUser(token);
        BaseConfig config = baseConfigService.current();
        return Response.data(config);
    }

    @PostMapping(path = "updateConfig")
    @ResponseBody
    public Response<Boolean> updateConfig(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated ConfigReq configReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        BaseConfig config = baseConfigService.current();
        BaseConfig delete = new BaseConfig();
        delete.setId(config.getId());
        delete.setDel(true);
        delete.setUpdateBy(accountId);
        if (!baseConfigService.updateById(delete)) {
            return Response.failed();
        }
        BaseConfig insert = new BaseConfig();
        insert.setCreateBy(accountId);
        insert.setUpdateBy(accountId);
        insert.setModelId(configReq.getModelId());
        insert.setFreqSecondLimit(configReq.getFreqSecondLimit());
        insert.setDailyRequestLimit(configReq.getDailyRequestLimit());
        insert.setMaxResultTokens(configReq.getMaxResultTokens());
        insert.setTemperature(configReq.getTemperature());
        insert.setSetSystem(configReq.getSystem());
        if (baseConfigService.save(insert)) {
            CardCache.clearConfig();
        }
        return Response.success();
    }

    @GetMapping(path = "key")
    @ResponseBody
    public Response<List<BaseKey>> keyList(
            @RequestHeader(name = "token", required = false) String token
    ) {
        AuthHelper.checkAdminUser(token);
        List<BaseKey> keyList = baseKeyService.list();
        return Response.data(keyList);
    }


    @PostMapping(path = "saveKey")
    @ResponseBody
    public Response<Boolean> updateKey(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated KeyReq keyReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        BaseKey save = new BaseKey();
        save.setApiKey(keyReq.getApiKey());
        save.setWeight(keyReq.getWeight());
        save.setState(keyReq.getState());
        save.setUpdateBy(accountId);
        if (null == keyReq.getId()) {
            save.setCreateBy(accountId);
            return Response.data(baseKeyService.save(save));
        }
        save.setId(keyReq.getId());
        return Response.data(baseKeyService.updateById(save));
    }

    @GetMapping(path = "appConfig")
    @ResponseBody
    public Response<List<AppConfigDTO>> appConfigMap(
            @RequestHeader(name = "token", required = false) String token
    ) {
        AuthHelper.checkAdminUser(token);
        Map<String, AppConfigDTO> configMap = ConfigCache.getAllAppConfig();
        return Response.data(new ArrayList<>(configMap.values()));
    }

    @PostMapping(path = "setAppConfig")
    @ResponseBody
    public Response<Boolean> setAppConfig(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated AppConfigDTO appConfig
    ) {
        AuthHelper.checkAdminUser(token);
        ConfigCache.setAppConfig(appConfig);
        return Response.success();
    }

    @GetMapping(path = "appNotice")
    @ResponseBody
    public Response<AppNoticeDTO> appNotice(
            @RequestHeader(name = "token", required = false) String token
    ) {
        AuthHelper.checkAdminUser(token);
        AppNoticeDTO appNotice = ConfigCache.getAppNotice();
        return Response.data(appNotice);
    }

    @PostMapping(path = "setAppNotice")
    @ResponseBody
    public Response<Boolean> setAppNotice(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated AppNoticeDTO appNotice
    ) {
        AuthHelper.checkAdminUser(token);
        ConfigCache.setAppNotice(appNotice);
        return Response.success();
    }
}
