package org.dominate.achp.common.enums;

/**
 * 卡密记录状态枚举
 *
 * @author dominate
 * @since 2022/01/19
 */
public enum CardRecordState {
    /**
     * 卡密记录状态
     */
    NOT_USE(0, "未使用"),
    USING(1, "使用中"),
    USED(2, "已使用"),
    DISABLED(3, "禁用"),
    WAIT(4, "待使用");

    final int code;
    final String name;

    CardRecordState(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static CardRecordState getValueByCode(int code) {
        for (CardRecordState state : CardRecordState.values()) {
            if (code == state.code) {
                return state;
            }
        }
        return DISABLED;
    }
}
