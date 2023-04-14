package org.dominate.achp.controller;

import lombok.AllArgsConstructor;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.common.helper.CardHelper;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.BaseKey;
import org.dominate.achp.entity.req.ConfigReq;
import org.dominate.achp.entity.req.KeyReq;
import org.dominate.achp.service.IBaseConfigService;
import org.dominate.achp.service.IBaseKeyService;
import org.dominate.achp.sys.Response;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if(baseConfigService.save(insert)){
            CardHelper.clearConfig();
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
        AuthHelper.checkAdminUser(token);
        BaseKey save = new BaseKey();
        save.setApiKey(keyReq.getApiKey());
        save.setWeight(keyReq.getWeight());
        save.setState(keyReq.getState());
        if (null == keyReq.getId()) {
            return Response.data(baseKeyService.save(save));
        }
        save.setId(keyReq.getId());
        return Response.data(baseKeyService.updateById(save));
    }

}
