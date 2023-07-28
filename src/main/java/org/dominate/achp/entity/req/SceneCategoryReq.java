package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 对话场景分类
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Getter
@Setter
@Accessors(chain = true)
public class SceneCategoryReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 分类类型 角色/场景
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String desr;

    /**
     * 是否删除
     */
    private Boolean del;

    private Integer seq;

    private String info;



}
