package org.dominate.achp.entity.dto;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dominate.achp.common.enums.SceneItemType;

@Getter
@Setter
@Accessors(chain = true)
public class SceneItemLimitDTO extends SceneItemBaseDTO {

    /**
     * 最大字数
     */
    private Integer max;

    @Override
    public SceneItemBaseDTO parseJson(String json, SceneItemType itemType, Integer itemId) {
        SceneItemBaseDTO item = JSON.parseObject(json, SceneItemLimitDTO.class);
        item.setId(itemId);
        item.setType(itemType);
        return item;
    }
}
