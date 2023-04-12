package org.dominate.achp.entity.dto;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dominate.achp.common.enums.SceneItemType;

@Getter
@Setter
@Accessors(chain = true)
public class SceneItemInputDTO extends SceneItemBaseDTO {

    /**
     * 可输入最大字数 0为不限制
     */
    private Integer limit;

    @Override
    public SceneItemBaseDTO parseJson(String json, SceneItemType itemType, Integer itemId) {
        SceneItemBaseDTO item = JSON.parseObject(json, SceneItemInputDTO.class);
        item.setId(itemId);
        item.setType(itemType);
        return item;
    }
}
