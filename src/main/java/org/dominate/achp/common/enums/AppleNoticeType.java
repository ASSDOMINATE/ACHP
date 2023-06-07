package org.dominate.achp.common.enums;

/**
 * 苹果通知类型枚举
 *
 * @author dominate
 * @since 2023-04-13
 */
public enum AppleNoticeType {
    /**
     * 购买类型
     */
    NO_FOLLOW_UP("无后续行动",0),

    REFUND("取消购买", 1),
    DID_RENEW("订阅续费", 2),
    RENEW_DISABLED("订阅取消",3)
    ;

    final String name;
    final int code;

    AppleNoticeType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static AppleNoticeType getValueByCode(int code) {
        for (AppleNoticeType value : AppleNoticeType.values()) {
            if (code == value.code) {
                return value;
            }
        }
        return NO_FOLLOW_UP;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

}
