package org.dominate.achp.common.enums;

import java.util.Objects;

/**
 * GPT 模型枚举
 *
 * @author dominate
 * @since 2022-04-13
 */
public enum GptModelType {
    /**
     * 模型
     */
    DEFAULT("gpt-3.5-turbo", 4, 4096),
    GPT_3_5_TURBO_0301("gpt-3.5-turbo-0301",4,4096),
    GPT_3_5_TURBO("gpt-3.5-turbo", 4, 4096);
    /**
     * 模型ID
     */
    final String id;
    /**
     * 模型计算数
     */
    final int tokenNum;
    /**
     * 模型Token数量限制
     */
    final int tokenLimit;

    GptModelType(String id, int tokenNum, int tokenLimit) {
        this.id = id;
        this.tokenNum = tokenNum;
        this.tokenLimit = tokenLimit;
    }

    public static GptModelType getModelType(String id) {
        for (GptModelType value : GptModelType.values()) {
            if (Objects.equals(id, value.id)) {
                return value;
            }
        }
        return DEFAULT;
    }

    public String getId() {
        return id;
    }

    public int getTokenNum() {
        return tokenNum;
    }

    public int getTokenLimit() {
        return tokenLimit;
    }
}
