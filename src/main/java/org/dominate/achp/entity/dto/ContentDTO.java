package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 聊天内容
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class ContentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 上一内容ID
     */
    private Integer lastId;


    /**
     * 问句
     */
    private String sentence;

    /**
     * 回复
     */
    private String reply;

    private Long createTime;



}
