package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.BasePaymentRecord;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.service.IBasePaymentRecordService;
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
@RequestMapping("/admin/base/")
@AllArgsConstructor
public class CardController {

    private final IUserInfoService userInfoService;

    private final IBasePaymentRecordService basePaymentRecordService;

    @GetMapping(path = "payment")
    @ResponseBody
    public Response<List<BasePaymentRecord>> paymentList(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sys_code", required = false) String sysCode,
            @RequestParam(name = "party_code", required = false) String partyCode,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);
        int accountId = 0;
        if (StringUtil.isNotEmpty(keyword)) {
            accountId = userInfoService.find(keyword);
        }
        QueryWrapper<BasePaymentRecord> query = new QueryWrapper<>();
        query.lambda().eq(accountId != 0, BasePaymentRecord::getAccountId, accountId)
                .eq(StringUtil.isNotEmpty(sysCode), BasePaymentRecord::getOrderCode, sysCode)
                .eq(StringUtil.isNotEmpty(partyCode), BasePaymentRecord::getPartyCode, partyCode)
                .last(SqlUtil.pageLimit(page.getSize(), page.getSize()));
        List<BasePaymentRecord> recordList = basePaymentRecordService.list(query);
        return Response.data(recordList);
    }


}
