package org.dominate.achp.common.enums;

/**
 * 存在值检查枚举
 *
 * @author dominate
 * @since 2022/06/21
 */
public enum ExistedType {
    /**
     * 存在值检查默认值
     */
    IDENTITY(1, "身份证号"),
    PHONE(2, "电话号"),
    MAIL(3, "邮箱");

    final int code;
    final String name;

    ExistedType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ExistedType getValueByCode(int code) {
        for (ExistedType stateEnum : ExistedType.values()) {
            if (code == stateEnum.code) {
                return stateEnum;
            }
        }
        return IDENTITY;
    }
}
