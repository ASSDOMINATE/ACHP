package org.dominate.achp.common.helper;

import com.hwja.tool.utils.HttpUtil;
import com.hwja.tool.utils.LoadUtil;
import com.hwja.tool.utils.RandomUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.common.utils.EncryptionUtil;
import org.dominate.achp.sys.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

@Slf4j
public final class WeChatPayHelper {

    /**
     * 商户平台设置的密钥key
     */
    private static final String PLATFORM_KEY = LoadUtil.getProperty("wechat.pay.platform.key");
    private static final String APP_ID = LoadUtil.getProperty("wechat.pay.app.id");
    private static final String MCH_ID = LoadUtil.getProperty("wechat.pay.mch.id");
    private static final String WX_PAY_CALLBACK = LoadUtil.getProperty("wechat.pay.callback");

    private static final String CREATE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static final String[] CREATE_PAY_ORDER_FOR_APP_KEYS = {"mch_id", "appid", "trade_type", "notify_url", "body", "total_fee", "out_trade_no", "nonce_str", "spbill_create_ip"};

    private static final String QUERY_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    private static final String[] QUERY_PAY_ORDER_KEYS = {"mch_id", "appid", "out_trade_no", "nonce_str"};

    /**
     * 返回结果常量
     */
    private static final String RESULT_TYPE = "result_code";
    private static final String RESULT_SUCCESS = "SUCCESS";
    private static final String PREPAY_ID = "prepay_id";
    private static final String TOTAL_FEE = "total_fee";
    private static final String TRADE_TYPE_APP = "APP";

    private static final String IP_ADDRESS;

    static {
        IP_ADDRESS = getIpAddress();
    }


    /**
     * 创建支付订单 用于 APP支付
     *
     * @param uniqueOrderCode 唯一订单号
     * @param payNum          支付金额
     * @param payTitle        支付标题
     * @return String 微信支付订单号，若为空字符串即请求失败
     */
    public static String createPayOrder(String uniqueOrderCode, String payNum, String payTitle) {
        String nonceStr = createNonceStr();
        String[] values = {MCH_ID, APP_ID, TRADE_TYPE_APP, WX_PAY_CALLBACK, payTitle, payNum, uniqueOrderCode, nonceStr, IP_ADDRESS};
        try {
            String response = sendRequest(CREATE_ORDER_URL, CREATE_PAY_ORDER_FOR_APP_KEYS, values);
            Map<String, Object> resultMap = WeChatPayHelper.parseXML(response);
            String returnCode = (String) resultMap.get(RESULT_TYPE);
            if (!RESULT_SUCCESS.equals(returnCode)) {
                return StringUtils.EMPTY;
            }
            return (String) resultMap.get(PREPAY_ID);
        } catch (Exception e) {
            log.error("WeChat create pay order error ", e);
            return StringUtils.EMPTY;
        }
    }


    /**
     * 查询订单
     *
     * @param uniqueOrderCode 唯一订单号
     * @return 微信返回XML结果
     * <p>
     * key:trade_state, value:SUCCESS—支付成功/REFUND—转入退款/NOTPAY—未支付/CLOSED—已关闭/REVOKED—已撤销（刷卡支付）/USERPAYING--用户支付中/PAYERROR--支付失败(其他原因，如银行返回失败)
     * key:total_fee, value:0.00
     */
    public static void verifyPayOrder(String uniqueOrderCode, BigDecimal balance) {
        String nonceStr = createNonceStr();
        String[] values = {MCH_ID, APP_ID, uniqueOrderCode, nonceStr};
        String response = sendRequest(QUERY_ORDER_URL, QUERY_PAY_ORDER_KEYS, values);
        Map<String, Object> responseMap = parseXML(response);
        if (!isSuccess(responseMap)) {
            throw BusinessException.create(ExceptionType.PAY_NOT_COMPLETED);
        }
        if (balance.compareTo(parsePayNum(responseMap)) != 0) {
            throw BusinessException.create(ExceptionType.PAY_PRICE_ERROR);
        }
    }

    /**
     * 判断请求是否成功
     *
     * @param resultMap 微信返回Map结果
     * @return 是否成功
     */
    public static boolean isSuccess(Map<String, Object> resultMap) {
        return RESULT_SUCCESS.equals(resultMap.get(RESULT_TYPE));
    }

    /**
     * 判断请求是否成功
     *
     * @param resultXml 微信返回Xml结果
     * @return 是否成功
     */
    public static boolean isSuccess(String resultXml) {
        return RESULT_SUCCESS.equals(parseXML(resultXml).get(RESULT_TYPE));
    }

    /**
     * 解析支付订单结果中的金额
     *
     * @param payResultMap 微信返回Map结果
     * @return 订单金额
     */
    public static BigDecimal parsePayNum(Map<String, Object> payResultMap) {
        final BigDecimal rate = new BigDecimal("100");
        try {
            // 微信的支付价格单位为分
            String totalFee = payResultMap.get(TOTAL_FEE).toString();
            if (!StringUtil.isNumeric(totalFee)) {
                return BigDecimal.ZERO;
            }
            BigDecimal thisFee = new BigDecimal(totalFee);
            return thisFee.divide(rate, RoundingMode.DOWN);
        } catch (Exception e) {
            log.error("WeChat parse pay result error ", e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取服务器IP
     *
     * @return 服务器IP
     */
    private static String getIpAddress() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("load server ip address error ", e);
        }
        return "";
    }


    private static Map<String, Object> parseXML(String xml) {
        Map<String, Object> map = new HashMap<>();
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            List<Element> list = rootElt.elements();
            for (Element element : list) {
                map.put(element.getName(), element.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 微信签名
     *
     * @param map 参数Map
     * @param key 签名Key
     * @return 签名字符串
     */
    private static String signature(Map<String, String> map, String key) {
        Set<String> keySet = map.keySet();
        String[] str = new String[map.size()];
        StringBuilder tmp = new StringBuilder();
        // 进行字典排序
        str = keySet.toArray(str);
        Arrays.sort(str);
        for (String s : str) {
            tmp.append(s).append("=").append(map.get(s)).append("&");
        }
        if (null != key) {
            tmp.append("key=").append(key);
        }
        return Objects.requireNonNull(EncryptionUtil.encryptMd5(tmp.toString())).toUpperCase();
    }

    /**
     * 处理参数为微信要求的XML
     *
     * @param keys   键数组
     * @param values 值数组
     * @return XML字符串
     */
    private static String formatRequestXML(String[] keys, String[] values) {
        assert keys.length == values.length;

        Map<String, String> paramMap = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            paramMap.put(keys[i], values[i]);
        }
        String sign = signature(paramMap, PLATFORM_KEY);
        StringBuilder xmlStr = new StringBuilder();
        xmlStr.append("<xml>");
        for (int i = 0; i < keys.length; i++) {
            xmlStr.append("<");
            xmlStr.append(keys[i]);
            xmlStr.append(">");
            xmlStr.append(values[i]);
            xmlStr.append("</");
            xmlStr.append(keys[i]);
            xmlStr.append(">");
        }
        xmlStr.append("<sign>");
        xmlStr.append(sign);
        xmlStr.append("</sign></xml>");
        return xmlStr.toString();
    }


    /**
     * 发起请求
     *
     * @param url    请求地址
     * @param keys   键数组
     * @param values 值数组
     * @return 返回结果
     */
    private static String sendRequest(String url, String[] keys, String[] values) {
        String xml = formatRequestXML(keys, values);
        return HttpUtil.sendPost(url, xml);
    }

    private static String createNonceStr() {
        return RandomUtil.getStringRandom(32);
    }

}
