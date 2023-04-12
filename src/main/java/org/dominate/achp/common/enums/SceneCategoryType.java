package org.dominate.achp.common.enums;

public enum SceneCategoryType {
    SCENE("场景", 1),
    ROLE("角色", 2),
    ;

    final String name;
    final int code;

    SceneCategoryType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static SceneCategoryType getValueByCode(int code) {
        for (SceneCategoryType value : SceneCategoryType.values()) {
            if (code == value.code) {
                return value;
            }
        }
        return SCENE;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
