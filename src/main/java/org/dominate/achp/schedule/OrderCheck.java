package org.dominate.achp.schedule;

import com.hwja.tool.utils.LoadUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.cache.PayOrderCache;
import org.dominate.achp.common.enums.PayType;
import org.dominate.achp.common.helper.AliPayHelper;
import org.dominate.achp.common.helper.ApplePayHelper;
import org.dominate.achp.common.helper.WeChatPayHelper;
import org.dominate.achp.entity.BaseCard;
import org.dominate.achp.entity.BasePaymentRecord;
import org.dominate.achp.entity.dto.PayOrderDTO;
import org.dominate.achp.service.IBaseCardRecordService;
import org.dominate.achp.service.IBaseCardService;
import org.dominate.achp.service.IBasePaymentRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * 订单检查
 *
 * @author dominate
 * @since 2023-04-14
 */
@Slf4j
@Component
@AllArgsConstructor
public class OrderCheck {

    private final IBaseCardService baseCardService;
    private final IBaseCardRecordService baseCardRecordService;
    private final IBasePaymentRecordService basePaymentRecordService;

    /**
     * 每分钟重试一次
     */
    private static final long CHECK_BETWEEN_TIME = 60 * 1000;

    /**
     * 60分钟还未完成算超时
     */
    private static final long ORDER_OUT_TIME = 60 * 60 * 1000;


    @Scheduled(cron = "*/10 * * * * ?")
    public void checkList() {
        if (!LoadUtil.onProd()) {
            return;
        }
        long thisTime = System.currentTimeMillis();
        Collection<PayOrderDTO> payOrders = PayOrderCache.getList();
        for (PayOrderDTO payOrder : payOrders) {
            if (StringUtil.isEmpty(payOrder.getSysOrderCode()) || StringUtil.isEmpty(payOrder.getPartyOrderCode())) {
                PayOrderCache.remove(payOrder.getSysOrderCode());
                return;
            }
            // 订单时间检查
            if (null != payOrder.getCheckedTime()) {
                // 1.未到检查间隔时间
                if (payOrder.getCheckedTime() + CHECK_BETWEEN_TIME > thisTime) {
                    continue;
                }
                // 2.超过过期时间
                if (payOrder.getCreateTime() + ORDER_OUT_TIME < thisTime) {
                    BasePaymentRecord paymentRecord = createFailedRecord(payOrder);
                    if (basePaymentRecordService.save(paymentRecord)) {
                        PayOrderCache.remove(payOrder.getSysOrderCode());
                        continue;
                    }
                }
            }
            BaseCard card = baseCardService.getById(payOrder.getCardId());
            // 3.订单支付检查
            if (!checkPay(payOrder, card)) {
                // 支付失败
                payOrder.setCheckedTime(thisTime);
                PayOrderCache.updateCheckTime(payOrder);
                continue;
            }
            log.info("支付订单校验通过 {} - {}", payOrder.getSysOrderCode(), payOrder.getPartyOrderCode());
            if (!basePaymentRecordService.isUniqueOrder(payOrder.getPartyOrderCode(), payOrder.getPayType())) {
                payOrder.setCheckedTime(thisTime);
                PayOrderCache.updateCheckTime(payOrder);
                continue;
            }
            int cardRecordId = baseCardRecordService.bindRecord(payOrder.getAccountId(), card);
            // 4.完成订单
            if (cardRecordId != 0) {
                basePaymentRecordService.save(payOrder, card, cardRecordId);
                PayOrderCache.remove(payOrder.getSysOrderCode());
                continue;
            }
            // 5.数据库异常导致保存失败，清理数据，等待下一次重试
            payOrder.setCheckedTime(thisTime);
            PayOrderCache.updateCheckTime(payOrder);
        }
    }

    private static boolean checkPay(PayOrderDTO payOrder, BaseCard card) {
        PayType payType = PayType.getValueByDbCode(payOrder.getPayType());
        try {
            switch (payType) {
                case APPLE:
                    ApplePayHelper.verifyPay(payOrder.getAuth(), payOrder.getPartyOrderCode(), card.getProductCode());
                    return true;
                case ALIPAY:
                    // 校验金额
                    AliPayHelper.verifyPayOrder(payOrder.getSysOrderCode(), card.getBalance());
                    return true;
                case WECHAT:
                case WECHAT_NATIVE:
                    // 校验金额
                    WeChatPayHelper.verifyPayOrder(payOrder.getSysOrderCode(), card.getBalance());
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            log.info("订单检查未通过 用户ID {} ，系统订单号 {} ，三方订单号 {}，订单生成时间 {}",
                    payOrder.getAccountId(), payOrder.getSysOrderCode(), payOrder.getPartyOrderCode(), payOrder.getCreateTime());
            return false;
        }

    }

    private static BasePaymentRecord createFailedRecord(PayOrderDTO payOrder) {
        BasePaymentRecord record = new BasePaymentRecord();
        record.setAccountId(payOrder.getAccountId());
        record.setType(payOrder.getPayType());
        record.setPrice(BigDecimal.ZERO);
        record.setOrderCode(payOrder.getSysOrderCode());
        record.setPartyCode(payOrder.getPartyOrderCode());
        PayType type = PayType.getValueByDbCode(payOrder.getPayType());
        String message = type.getName() + " 支付超时未完成";
        record.setMessage(message);
        record.setComment(StringUtil.EMPTY);
        return record;
    }
}
