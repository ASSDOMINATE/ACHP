package org.dominate.achp.common.enums;

/**
 * 场景计数枚举
 *
 * @author dominate
 * @since 2023-04-13
 */
public enum SceneCountType {
    /**
     * 计数类型
     */
    READ("read", 1),
    SEND("send", 2),
    CHAT("chat", 3),
    ;

    final String name;
    final int code;

    SceneCountType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static SceneCountType getValueByCode(int code) {
        for (SceneCountType value : SceneCountType.values()) {
            if (code == value.code) {
                return value;
            }
        }
        return READ;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

}
