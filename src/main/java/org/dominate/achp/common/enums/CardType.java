package org.dominate.achp.common.enums;

/**
 * 卡密类型枚举
 *
 * @author dominate
 * @since 2023-04-13
 */
public enum CardType {
    /**
     * 卡密类型
     */
    DAY("天数限制", 1),
    COUNT("次数限制", 2),
    ;

    final String name;
    final int code;

    CardType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static CardType getValueByCode(int code){
        for (CardType value : CardType.values()) {
            if(code == value.code){
                return value;
            }
        }
        return DAY;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
