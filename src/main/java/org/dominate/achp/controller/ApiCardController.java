package org.dominate.achp.controller;

import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.PayOrderCache;
import org.dominate.achp.common.enums.BuyType;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.common.enums.PayType;
import org.dominate.achp.common.enums.ResponseType;
import org.dominate.achp.common.helper.AliPayHelper;
import org.dominate.achp.common.helper.ApplePayHelper;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.common.helper.WeChatPayHelper;
import org.dominate.achp.common.utils.UniqueCodeUtil;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BaseCardRecord;
import org.dominate.achp.entity.dto.*;
import org.dominate.achp.entity.req.AppleResumeReq;
import org.dominate.achp.entity.req.ExchangeReq;
import org.dominate.achp.entity.req.PayOrderReq;
import org.dominate.achp.entity.req.PayReq;
import org.dominate.achp.service.CardService;
import org.dominate.achp.service.IBaseCardRecordService;
import org.dominate.achp.service.IBaseCardService;
import org.dominate.achp.service.IBasePaymentRecordService;
import org.dominate.achp.sys.Response;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/card")
@AllArgsConstructor
public class ApiCardController {

    private final CardService cardService;

    private final IBaseCardService baseCardService;
    private final IBaseCardRecordService baseCardRecordService;
    private final IBasePaymentRecordService basePaymentRecordService;

    @GetMapping(path = "getEnableCard")
    @ResponseBody
    public Response<List<CardDTO>> getEnableCard(
            @RequestParam(value = "all", defaultValue = "false", required = false) Boolean all
    ) {
        List<CardDTO> cardList = baseCardService.enableList();
        if (all) {
            return Response.data(cardList);
        }
        List<CardDTO> commonCardList = new ArrayList<>(cardList.size());
        for (CardDTO card : cardList) {
            if (BuyType.COMMON.getCode() == card.getBuyType()) {
                commonCardList.add(card);
            }
        }
        return Response.data(commonCardList);
    }

    @GetMapping(path = "getUserCard")
    @ResponseBody
    public Response<List<CardRecordDTO>> getUserCard(
            @RequestHeader String token
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        List<CardRecordDTO> recordList = baseCardRecordService.userRecordList(accountId);
        return Response.data(recordList);
    }

    @GetMapping(path = "checkUserCard")
    @ResponseBody
    public Response<CardRecordDTO> checkUserCard(
            @RequestHeader String token
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        CardRecordDTO record = cardService.checkUserRecord(accountId);
        String waitRecordInfo = baseCardRecordService.getRecordWaitInfo(accountId);
        record.setInfo(record.getInfo() + waitRecordInfo);
        return Response.data(record);
    }

    @PostMapping(path = "createPayUrl")
    @ResponseBody
    public Response<PayResultDTO> createPayUrl(
            @RequestHeader String token,
            @Validated @RequestBody PayReq payReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        BaseCard card = baseCardService.getById(payReq.getCardId());
        String sysOrderCode = UniqueCodeUtil.createPayOrder(payReq.getPayType());
        PayResultDTO payResult;
        switch (PayType.getValueByDbCode(payReq.getPayType())) {
            case WECHAT_NATIVE:
                payResult = WeChatPayHelper.createNativePayOrder(sysOrderCode, card.getBalance(), card.getName());
                break;
            case WECHAT:
            case ALIPAY:
            case APPLE:
                throw BusinessException.create(ExceptionType.PAY_ORDER_TYPE_ERROR);
            default:
                throw BusinessException.create(ExceptionType.PARAM_ERROR);
        }
        payReq.setOrderCode(sysOrderCode);
        payResult.setSysOrderCode(sysOrderCode);
        PayOrderDTO payOrder = new PayOrderDTO(payReq);
        payOrder.setAccountId(accountId);
        payOrder.setSysOrderCode(sysOrderCode);
        payOrder.setPartyOrderCode(payResult.getPartyOrderCode());
        PayOrderCache.save(payOrder);
        return Response.data(payResult);
    }

    @PostMapping(path = "createPayOrder")
    @ResponseBody
    public Response<String> createPayOrder(
            @RequestHeader String token,
            @Validated @RequestBody PayReq payReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        BaseCard card = baseCardService.getById(payReq.getCardId());
        String sysOrderCode = UniqueCodeUtil.createPayOrder(payReq.getPayType());
        String partyOrderCode;
        switch (PayType.getValueByDbCode(payReq.getPayType())) {
            case WECHAT:
                partyOrderCode = WeChatPayHelper.createAppPayOrder(sysOrderCode, card.getBalance(), card.getName());
                break;
            case WECHAT_NATIVE:
            case ALIPAY:
            case APPLE:
                throw BusinessException.create(ExceptionType.PAY_ORDER_TYPE_ERROR);
            default:
                throw BusinessException.create(ExceptionType.PARAM_ERROR);
        }
        payReq.setOrderCode(sysOrderCode);
        PayOrderDTO payOrder = new PayOrderDTO(payReq);
        payOrder.setAccountId(accountId);
        payOrder.setSysOrderCode(sysOrderCode);
        payOrder.setPartyOrderCode(partyOrderCode);
        PayOrderCache.save(payOrder);
        return Response.data(partyOrderCode);
    }

    @PostMapping(path = "savePayOrder")
    @ResponseBody
    public Response<Boolean> savePayOrder(
            @RequestHeader String token,
            @Validated @RequestBody PayReq payReq
    ) {
        if (StringUtil.isEmpty(payReq.getOrderCode())) {
            return Response.failed();
        }
        int accountId = AuthHelper.parseWithValidForId(token);
        String sysOrderCode = UniqueCodeUtil.createPayOrder(payReq.getPayType());
        if (null == payReq.getCardId()) {
            BaseCard card = baseCardService.findCardForProduct(payReq.getProductCode());
            payReq.setCardId(card.getId());
        }
        PayOrderDTO order = new PayOrderDTO(payReq);
        order.setAccountId(accountId);
        order.setSysOrderCode(sysOrderCode);
        order.setPartyOrderCode(payReq.getOrderCode());
        PayOrderCache.save(order);
        return Response.success();
    }

    @PostMapping(path = "resumeApple")
    @ResponseBody
    public Response<Boolean> resumeApple(
            @RequestHeader String token,
            @Validated @RequestBody AppleResumeReq resumeReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        List<AppleProductDTO> productList = ApplePayHelper.parseProductList(resumeReq.getReceiptDate());
        // 一般不凭证中不会有多个产品，故只会循环1次暂时不用优化数据库操作
        for (AppleProductDTO product : productList) {
            BaseCard card = baseCardService.findCardForProduct(product.getProductCode());
            String partyOrder = product.getTransactionId();
            if (!basePaymentRecordService.isUniqueOrder(partyOrder, PayType.APPLE.getDbCode())) {
                continue;
            }
            int cardRecordId = baseCardRecordService.bindRecord(accountId, card);
            basePaymentRecordService.save(accountId, partyOrder, PayType.APPLE, card, cardRecordId);
        }
        return Response.code(ResponseType.RESUME_SUCCESS);
    }

    @PostMapping(path = "checkPayOrder")
    @ResponseBody
    public Response<Boolean> checkPayOrder(
            @RequestHeader String token,
            @Validated @RequestBody PayOrderReq payOrderReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        PayOrderDTO cacheOrder = PayOrderCache.find(payOrderReq.getPayType(), payOrderReq.getOrderCode());
        if (null == cacheOrder) {
            throw BusinessException.create(ExceptionType.PAY_ORDER_NOT_FOUND);
        }
        if (accountId != cacheOrder.getAccountId()) {
            throw BusinessException.create(ExceptionType.PAY_ORDER_MUST_SAME_USER);
        }
        BaseCard card = baseCardService.getById(cacheOrder.getCardId());
        switch (PayType.getValueByDbCode(payOrderReq.getPayType())) {
            case APPLE:
                ApplePayHelper.verifyPay(cacheOrder.getAuth(), cacheOrder.getPartyOrderCode(), card.getProductCode());
                break;
            case ALIPAY:
                // 校验金额
                AliPayHelper.verifyPayOrder(cacheOrder.getPartyOrderCode(), card.getBalance());
                break;
            case WECHAT:
            case WECHAT_NATIVE:
                // 校验金额
                WeChatPayHelper.verifyPayOrder(cacheOrder.getPartyOrderCode(), card.getBalance());
                break;
            default:
                throw BusinessException.create(ExceptionType.PARAM_ERROR);
        }
        if (!basePaymentRecordService.isUniqueOrder(cacheOrder)) {
            throw BusinessException.create(ExceptionType.PAY_NOT_COMPLETED);
        }
        int cardRecordId = baseCardRecordService.bindRecord(accountId, card);
        if (cardRecordId != 0) {
            basePaymentRecordService.save(cacheOrder, card, cardRecordId);
            PayOrderCache.remove(cacheOrder.getSysOrderCode());
            return Response.success();
        }
        return Response.failed();
    }

    @PostMapping(path = "exchangeCard")
    @ResponseBody
    public Response<Boolean> exchangeCard(
            @RequestHeader String token,
            @Validated @RequestBody ExchangeReq exchangeReq) {
        int accountId = AuthHelper.parseWithValidForId(token);
        BaseCardRecord record = baseCardRecordService.findActiveRecord(exchangeReq.getExchangeKey());
        if (Optional.ofNullable(record).isEmpty()) {
            throw BusinessException.create(ExceptionType.NOT_FOUND_CARD);
        }
        try {
            cardService.checkUserRecord(accountId);
            throw BusinessException.create(ExceptionType.HAS_CARD_BINDING);
        } catch (BusinessException e) {
            // 检查记录状态出现异常代表没有可以用的卡
        }
        BaseCard card = baseCardService.getById(record.getCardId());
        return Response.data(baseCardRecordService.bindRecord(accountId, record.getId(), card));
    }
}
