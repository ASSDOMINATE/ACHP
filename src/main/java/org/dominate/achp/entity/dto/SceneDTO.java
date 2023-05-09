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
public class SceneDTO implements Serializable {

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

    private String imgSrc;

    /**
     * 查看数量
     */
    private Integer readCount;

    /**
     * 发送数量
     */
    private Integer sendCount;

    /**
     * 会话数量
     */
    private Integer chatCount;

    private Boolean forRecommend;

    private Integer seq;




}
