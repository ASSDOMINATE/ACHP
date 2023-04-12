package org.dominate.achp.common.enums;

/**
 * 用户状态
 *
 * @author dominate
 * @since 2022/01/04
 */
public enum UserState {

    /**
     * 不存在的账号
     */
    NOT_FIND(-1,"不存在的账号", true),
    /**
     * 正常状态
     */
    NORMAL(0, "正常", false),
    /**
     * 被禁用的账号
     */
    DISABLED(1, "禁用", true),
    /**
     * 标记为删除的账号
     */
    DELETE(2, "删除", true),
    /**
     * 还未激活的账号
     */
    NOT_ACTIVE(3, "未激活", true),
    ;
    final int code;
    final String name;
    final boolean disabled;

    UserState(int code, String name, boolean disabled) {
        this.code = code;
        this.name = name;
        this.disabled = disabled;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public static UserState getValueByCode(int code) {
        for (UserState stateEnum : UserState.values()) {
            if (code == stateEnum.getCode()) {
                return stateEnum;
            }
        }
        return NOT_FIND;
    }
}
