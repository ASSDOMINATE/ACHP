package org.dominate.achp.common.enums;

/**
 * 对话失败类型
 *
 * @author dominate
 * @since 2023-04-13
 */
public enum ChatFailedType {

    EMPTY("请求内容有误，请调整后发送", "null"),
    CLIENT_CLOSE("客户端已中断连接"),
    CONTENT_TO_LONG("请求内容超过长度限制，请调整后发送"),
    NETWORK_BAD("网络连接较差，请稍后再试", "java.net.SocketTimeoutException: timeout"),
    MODEL_OVERLOADED("当前请求过多，模型已过载，请稍后再试", "That model is currently overloaded with other requests", "Rate limit reached"),
    MODEL_USE_ERROR("模型异常，请联系管理员", "Your access was terminated", "You exceeded your current quota"),
    ;

    final String result;
    final String[] signs;


    ChatFailedType(String result) {
        this.result = result;
        this.signs = new String[0];
    }

    ChatFailedType(String result, String... signs) {
        this.result = result;
        this.signs = signs;
    }

    public static ChatFailedType getValueByCode(String sign) {
        for (ChatFailedType value : ChatFailedType.values()) {
            if (sign.equals(value.result)) {
                return value;
            }
            for (String s : value.signs) {
                if (sign.startsWith(s)) {
                    return value;
                }
            }
        }
        return EMPTY;
    }

    public static String parseSign(String sign) {
        return getValueByCode(sign).result;
    }

    public String getResult() {
        return result;
    }

    public String[] getSigns() {
        return signs;
    }


}
