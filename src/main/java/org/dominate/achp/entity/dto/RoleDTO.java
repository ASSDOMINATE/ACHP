package org.dominate.achp.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户角色
 * </p>
 *
 * @author dominate
 * @since 2022-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer parentId;

    private Integer platformId;


    /**
     * 权限名称 最大长度15
     */
    private String name;

    /**
     * 描述 最大长度256
     */
    private String desr;



}
