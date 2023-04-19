package org.dominate.achp.common.enums;

public enum PlatformClientType {

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
}
