package org.dominate.achp.common.enums;

import org.dominate.achp.entity.dto.SceneItemBaseDTO;
import org.dominate.achp.entity.dto.SceneItemInputDTO;
import org.dominate.achp.entity.dto.SceneItemLimitDTO;
import org.dominate.achp.entity.dto.SceneItemSelectDTO;

/**
 * 场景项类型枚举
 *
 * @author dominate
 * @since 2023-04-04
 */
public enum SceneItemType {

    /**
     * 场景类型项
     */
    UNDEFINED("未定义", 0, new SceneItemBaseDTO()),

    INPUT("输入框", 1, new SceneItemInputDTO()),

    MULTIPLE_SELECT("多选", 2, new SceneItemSelectDTO()),

    SINGLE_SELECT("单选", 3, new SceneItemSelectDTO()),

    WORDS_LIMIT("字数限制", 4, new SceneItemLimitDTO()),
    ;

    final String name;
    final int code;
    final SceneItemBaseDTO item;

    SceneItemType(String name, int code, SceneItemBaseDTO item) {
        this.name = name;
        this.code = code;
        this.item = item;
    }

    public static SceneItemType getValueByCode(int code) {
        for (SceneItemType value : SceneItemType.values()) {
            if (code == value.code) {
                return value;
            }
        }
        return UNDEFINED;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public SceneItemBaseDTO getItem() {
        return item;
    }
}
