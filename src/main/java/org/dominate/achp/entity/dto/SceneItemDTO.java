package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 场景项
 *
 * @author dominate
 * @since 2023-04-14
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
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
