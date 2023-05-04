package org.dominate.achp.common.enums;

/**
 * Redis 缓存类型
 *
 * @author dominate
 * @since 2023-04-14
 */
public enum RedisCacheType {
    /**
     * 缓存类型
     */
    SET("键值对", 1),
    H_SET("哈希键值对", 2),
    LIST("列表", 3);

    final String name;
    final int code;

    RedisCacheType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static RedisCacheType getValueByCode(int code) {
        for (RedisCacheType value : RedisCacheType.values()) {
            if (code == value.code) {
                return value;
            }
        }
        return SET;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
