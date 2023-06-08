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
public class RolePermCheckDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 平台ID
     */
    private Integer platformId;

    /**
     * 角色名称 最大长度15
     */
    private String name;

    /**
     * 是否有该权限
     */
    private Boolean hasPerm;

    /**
     * 权限ID
     */
    private Integer permId;

}
