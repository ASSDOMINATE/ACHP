package org.dominate.achp.common.enums;

/**
 * 支付目标类型枚举
 *
 * @author dominate
 * @since 2023-04-13
 */
public enum PaymentTargetType {
    /**
     * 支付目标类型
     */
    CARD("卡密",0),
    ;

    final String name;
    final int code;

    PaymentTargetType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static PaymentTargetType getValueByCode(int code) {
        for (PaymentTargetType value : PaymentTargetType.values()) {
            if (code == value.code) {
                return value;
            }
        }
        return CARD;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

}
