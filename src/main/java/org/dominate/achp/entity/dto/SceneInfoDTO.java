package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dominate.achp.entity.ChatSceneCategory;
import org.dominate.achp.entity.ChatSceneConf;
import org.dominate.achp.entity.ChatSceneItem;

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
public class SceneInfoDTO extends SceneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 提醒
     */
    private String notice;

    private ChatSceneItem[] items;

    private ChatSceneConf[] configs;

    private ChatSceneCategory[] categories;


}
