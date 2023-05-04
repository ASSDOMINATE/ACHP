package org.dominate.achp.entity.dto;

import com.alibaba.fastjson.JSON;
import com.hwja.tool.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dominate.achp.common.enums.SceneItemType;


/**
 * 场景项输入框类型
 *
 * @author dominate
 * @since 2023-04-14
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SceneItemInputDTO extends SceneItemBaseDTO {

    private String value;

    @Override
    public SceneItemBaseDTO parseJson(String json, SceneItemType itemType, String title, Integer itemId) {
        SceneItemBaseDTO item = JSON.parseObject(json, SceneItemInputDTO.class);
        item.setId(itemId);
        item.setType(itemType);
        if(StringUtil.isNotEmpty(title)){
            item.setTypeName(title);
        }
        return item;
    }
}
