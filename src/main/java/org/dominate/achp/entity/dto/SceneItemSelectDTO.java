package org.dominate.achp.entity.dto;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dominate.achp.common.enums.SceneItemType;

@Getter
@Setter
@Accessors(chain = true)
public class SceneItemSelectDTO extends SceneItemBaseDTO {

    /**
     * 选项词数组
     */
    private String[] selectWords;

    /**
     * 最大选择数量
     */
    private Integer maxSelected;

    @Override
    public SceneItemBaseDTO parseJson(String json, SceneItemType itemType, Integer itemId) {
        SceneItemBaseDTO item = JSON.parseObject(json, SceneItemSelectDTO.class);
        item.setId(itemId);
        item.setType(itemType);
        return item;
    }
}
