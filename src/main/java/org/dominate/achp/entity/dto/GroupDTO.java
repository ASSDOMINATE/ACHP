package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 对话组
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 由程序生产唯一值
     */
    private String id;

    private String title;


}
