package org.dominate.achp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwja.tool.utils.SqlUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.BasePaymentRecord;
import org.dominate.achp.entity.req.CardReq;
import org.dominate.achp.entity.req.IdReq;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.service.IBaseCardRecordService;
import org.dominate.achp.service.IBaseCardService;
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
@RequestMapping("/admin/card/")
@AllArgsConstructor
public class CardController {

    private final IUserInfoService userInfoService;

    private final IBasePaymentRecordService basePaymentRecordService;
    private final IBaseCardService baseCardService;
    private final IBaseCardRecordService baseCardRecordService;

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
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()));
        List<BasePaymentRecord> recordList = basePaymentRecordService.list(query);
        return Response.data(recordList);
    }

    @GetMapping(path = "card")
    @ResponseBody
    public Response<List<BaseCard>> cardList(
            @RequestHeader(name = "token", required = false) String token
    ) {
        AuthHelper.checkAdminUser(token);
        List<BaseCard> cardList = baseCardService.list();
        return Response.data(cardList);
    }

    @PostMapping(path = "saveCard")
    @ResponseBody
    public Response<Boolean> saveCard(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody @Validated CardReq keyReq
    ) {
        int accountId = AuthHelper.parseWithValidAdminForId(token);
        BaseCard save = new BaseCard();
        save.setName(keyReq.getName());
        save.setDesr(keyReq.getDesr());
        save.setBalance(keyReq.getBalance());
        save.setProductCode(keyReq.getProductCode());
        save.setType(keyReq.getType());
        save.setCountLimit(keyReq.getCountLimit());
        save.setDayLimit(keyReq.getDayLimit());
        save.setStock(keyReq.getStock());
        save.setState(keyReq.getState());
        save.setUpdateBy(accountId);
        if (null == keyReq.getId()) {
            save.setCreateBy(accountId);
            return Response.data(baseCardService.save(save));
        }
        save.setId(keyReq.getId());
        return Response.data(baseCardService.updateById(save));
    }

    @PostMapping(path = "createCardRecord")
    @ResponseBody
    public Response<BaseCardRecord> createCardRecord(
            @RequestHeader(name = "token", required = false) String token,
            @RequestBody IdReq idReq
    ) {
        AuthHelper.checkAdminUser(token);
        BaseCard card = baseCardService.getById(idReq.getId());
        int recordId = baseCardRecordService.createRecord(card);
        return Response.data(baseCardRecordService.getById(recordId));
    }

    @GetMapping(path = "cardRecordList")
    @ResponseBody
    public Response<List<BaseCardRecord>> cardRecordList(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam(name = "card_id", required = false) Integer cardId,
            @RequestParam(name = "exchange_key", required = false) String key,
            @RequestParam(name = "keyword", required = false) String keyword,
            @Validated PageReq page
    ) {
        AuthHelper.checkAdminUser(token);

        int accountId = 0;
        if (StringUtil.isNotEmpty(keyword)) {
            accountId = userInfoService.find(keyword);
        }
        QueryWrapper<BaseCardRecord> query = new QueryWrapper<>();
        query.lambda().eq(null != cardId, BaseCardRecord::getCardId, cardId)
                .eq(StringUtil.isNotEmpty(key), BaseCardRecord::getExchangeKey, key)
                .eq(accountId != 0, BaseCardRecord::getAccountId, accountId)
                .last(SqlUtil.pageLimit(page.getSize(), page.getPage()))
                .orderByDesc(BaseCardRecord::getId);
        List<BaseCardRecord> recordList = baseCardRecordService.list(query);
        return Response.data(recordList);
    }


}
