package org.dominate.achp.common.enums;

/**
 * 用户三方绑定类型
 *
 * @author dominate
 * @since 2022/01/04
 */
public enum UserBindType {

    /**
     * 未知类型
     */
    UNKNOWN(0, "未知"),
    /**
     * 微信
     */
    WECHAT(1, "微信"),
    /**
     * 企业微信
     */
    WORK_WECHAT(2, "企业微信"),
    /**
     * 微博
     */
    WEIBO(3, "微博"),
    /**
     * EHR
     */
    EHR(4, "EHR", true),
    ;

    final int code;
    final String name;
    final boolean needValid;

    UserBindType(int code, String name) {
        this.code = code;
        this.name = name;
        this.needValid = false;
    }

    UserBindType(int code, String name, boolean needValid) {
        this.code = code;
        this.name = name;
        this.needValid = needValid;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isNeedValid() {
        return needValid;
    }

    public static UserBindType getValueByCode(int code) {
        for (UserBindType stateEnum : UserBindType.values()) {
            if (code == stateEnum.getCode()) {
                return stateEnum;
            }
        }
        return UNKNOWN;
    }
}
