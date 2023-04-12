package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
public class SceneItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer sceneId;

    private SceneItemBaseDTO item;

    /**
     * 排序数
     */
    private Integer seq;
}
