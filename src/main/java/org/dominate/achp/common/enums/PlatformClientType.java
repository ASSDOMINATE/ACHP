package org.dominate.achp.common.enums;

import com.hwja.tool.utils.StringUtil;

/**
 * 管理后台类型
 *
 * @author dominate
 * @since 2023-04-04
 */
public enum PlatformClientType {

    /**
     * 管理后台
     */
    ADMIN(0, "管理后台"),
    WEB(1, "网页"),
    APP(2, "APP");

    final int id;
    final String name;

    PlatformClientType(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static PlatformClientType getValueByCode(String code) {
        if (null == code) {
            return APP;
        }
        if (!StringUtil.isNumeric(code)) {
            return APP;
        }
        for (PlatformClientType value : PlatformClientType.values()) {
            if (value.id == Integer.parseInt(code)) {
                return value;
            }
        }
        return APP;
    }
}
