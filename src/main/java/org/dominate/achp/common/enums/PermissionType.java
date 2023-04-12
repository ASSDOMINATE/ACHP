package org.dominate.achp.common.enums;

/**
 * 权限类型
 *
 * @author dominate
 * @since 2022/01/04
 */
public enum PermissionType {

    /**
     * 全部类型
     */
    ALL(0, "全部权限"),
    /**
     * 菜单权限
     */
    MENU(1, "菜单权限"),
    /**
     * 数据权限
     */
    DATA(2, "数据权限"),
    /**
     * 接口权限
     */
    INTERFACE(3, "接口权限"),
    ;

    final int code;
    final String name;

    PermissionType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PermissionType getValueByCode(int code) {
        for (PermissionType stateEnum : PermissionType.values()) {
            if (code == stateEnum.getCode()) {
                return stateEnum;
            }
        }
        return ALL;
    }
}
