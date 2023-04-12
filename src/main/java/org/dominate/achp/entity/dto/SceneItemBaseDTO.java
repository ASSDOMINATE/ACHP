package org.dominate.achp.entity.dto;

import com.alibaba.fastjson.JSON;
import com.hwja.tool.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dominate.achp.common.enums.SceneItemType;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
public class SceneItemBaseDTO implements Serializable {

    private Integer id;

    /**
     * 类型 输入框 单选 多选 字数限制
     */
    private Integer typeCode;

    private String typeName;

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public String toDBJson() {
        id = null;
        typeCode = null;
        typeName = null;
        return JSON.toJSONString(this);
    }

    public void setType(SceneItemType itemType) {
        this.typeCode = itemType.getCode();
        this.typeName = itemType.getName();
    }

    public SceneItemBaseDTO parseJson(String json, SceneItemType itemType, String title, Integer itemId) {
        SceneItemBaseDTO item = JSON.parseObject(json, SceneItemBaseDTO.class);
        item.setId(itemId);
        item.setType(itemType);
        if(StringUtil.isNotEmpty(title)){
            item.setTypeName(title);
        }
        return item;
    }
}
