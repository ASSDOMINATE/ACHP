package org.dominate.achp.common.helper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hwja.tool.utils.HttpUtil;
import com.hwja.tool.utils.JsonUtil;
import com.hwja.tool.utils.LoadUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.sys.exception.BusinessException;

import java.util.HashMap;
import java.util.Map;

/**
 * 苹果支付工具
 *
 * @author dominate
 * @since 2023-04-27
 */
@Slf4j
public class ApplePayHelper {

    private static final String VERIFY_URL = "https://buy.itunes.apple.com/verifyReceipt";
    private static final String VERIFY_URL_SANDBOX = "https://sandbox.itunes.apple.com/verifyReceipt";

    private static final String REQUEST_RECEIPT_DATA = "receipt-data";
    private static final String[] RESPONSE_STATUS = {"status"};
    private static final String[] RESPONSE_APP_LIST = {"receipt", "in_app"};
    private static final String[] RESPONSE_BUNDLE = {"receipt", "bundle_id"};
    private static final String RESPONSE_TRANSACTION_ID = "transaction_id";
    private static final String RESPONSE_PRODUCT_ID = "product_id";

    private static final String SUCCESS_STATUS_CODE = "0";
    private static final String CHECK_ON_SANDBOX_CODE = "21007";

    private static final String BUNDLE_ID = LoadUtil.getProperty("apple.pay.bundle-id");
    private static final boolean USE_SANDBOX = LoadUtil.getBooleanProperty("apple.pay.sandbox");

    /**
     * 苹果支付回调验证
     * 0 正常
     * 21000 App Store不能读取你提供的JSON对象
     * 21002 receipt-data域的数据有问题
     * 21003 receipt无法通过验证
     * 21004 提供的shared secret不匹配你账号中的shared secret
     * 21005 receipt服务器当前不可用
     * 21006 receipt合法，但是订阅已过期。服务器接收到这个状态码时，receipt数据仍然会解码并一起发送
     * 21007 receipt是Sandbox receipt，但却发送至生产系统的验证服务
     * 21008 receipt是生产receipt，但却发送至Sandbox环境的验证服务
     *
     * @param receiptDate 数据凭证
     * @throws BusinessException 未抛出异常代表成功，否则在异常里返回失败原因
     */
    public static void verifyPay(String receiptDate, String orderCode, String productCode) throws BusinessException {
        //1.发送平台验证
        String response = ApplePayHelper.sendVerify(receiptDate, false);
        // 苹果服务器没有返回验证结果
        if (StringUtil.isEmpty(response)) {
            log.error("苹果验证失败 支付凭证 {}", receiptDate);
            throw BusinessException.create(ExceptionType.PAY_NOT_FOUND_ORDER);
        }
        // 2.苹果验证 返回结果
        String states = JsonUtil.parseResponseValueForString(response, RESPONSE_STATUS);
        // 3.沙盒测试
        if (USE_SANDBOX && CHECK_ON_SANDBOX_CODE.equals(states)) {
            response = ApplePayHelper.sendVerify(receiptDate, true);
            states = JsonUtil.parseResponseValueForString(response, RESPONSE_STATUS);
        }
        // 只有返回状态值为0是成功
        if (!SUCCESS_STATUS_CODE.equals(states)) {
            log.error("苹果验证不通过 结果 {} 支付凭证 {}", states, receiptDate);
            throw BusinessException.create(ExceptionType.PAY_NOT_COMPLETED);
        }
        // 4.验证支付来源
        String bundleId = JsonUtil.parseResponseValueForString(response, RESPONSE_BUNDLE);
        if (!BUNDLE_ID.equals(bundleId)) {
            log.error("苹果验证订单错误，有刷单嫌疑，请求订单号 [{}]，请求 BUNDLE ID [{}]", orderCode, bundleId);
            throw BusinessException.create(ExceptionType.PAY_ORDER_NOT_FOUND);
        }
        // 5.验证支付订单号及产品
        JSONArray apps = JsonUtil.parseResponseValueForJsonArray(response, RESPONSE_APP_LIST);
        for (int i = 0; i < apps.size(); ++i) {
            JSONObject app = apps.getJSONObject(i);
            String resultOrderCode = app.getString(RESPONSE_TRANSACTION_ID);
            if (!orderCode.equals(resultOrderCode)) {
                continue;
            }
            // 检查当前订单号
            String resultProductId = app.getString(RESPONSE_PRODUCT_ID);
            // 产品ID确认
            if (productCode.equals(resultProductId)) {
                return;
            }
            // 产品ID不对
            log.error("苹果验证订单错误，有刷单嫌疑，请求订单号 [{}]，请求产品ID [{}]，苹果产品ID [{}]", orderCode, productCode, resultProductId);
            throw BusinessException.create(ExceptionType.PAY_ORDER_NOT_FOUND);
        }
        // 凭证中无该订单号
        log.error("苹果验证订单错误，有刷单嫌疑，请求订单号 [{}]", orderCode);
        throw BusinessException.create(ExceptionType.PAY_ORDER_NOT_FOUND);
    }

    private static String sendVerify(String receiptDate, boolean onSandbox) {
        Map<String, Object> params = new HashMap<>(1);
        params.put(REQUEST_RECEIPT_DATA, receiptDate);
        return HttpUtil.sendPost(onSandbox ? VERIFY_URL_SANDBOX : VERIFY_URL, params, true);
    }

}