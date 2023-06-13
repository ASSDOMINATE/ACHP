package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 对话场景
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SceneDetailDTO extends SceneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 提醒
     */
    private String notice;


    private ContentDTO[] contents;

    /**
     * 场景项
     */
    private SceneItemBaseDTO[] items;

    private SceneCategoryDTO[] categories;


}
