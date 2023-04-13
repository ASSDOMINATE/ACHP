package org.dominate.achp.common.enums;

import java.util.Objects;

public enum GptModelType {
    DEFAULT("gpt-3.5-turbo", 3, 4096),
    GPT_3_5_TURBO("gpt-3.5-turbo", 3, 4096);
    final String id;
    final int tokenNum;
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
