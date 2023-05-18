package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
public class SceneInfoReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String desr;

    /**
     * 系统设置
     */
    private String system;

    /**
     * 提醒
     */
    private String notice;

    private Integer seq;

    private String imgSrc;

    private Boolean del;

    private ChatSceneItem[] items;

    private ChatSceneConf[] configs;

    private Integer[] categoryIds;

}
