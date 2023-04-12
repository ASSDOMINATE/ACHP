package org.dominate.achp.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户权限
 * </p>
 *
 * @author dominate
 * @since 2022-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer parentId;

    private Integer platformId;

    /**
     * 权限类型
     */
    private Integer typeCode;

    private String typeName;

    /**
     * 权限名称 最大长度15
     */
    private String name;

    /**
     * 描述 最大长度256
     */
    private String desr;

    /**
     * 唯一权限编码 最大长度45
     */
    private String code;

    /**
     * 权限对应路径 最大长度45
     */
    private String path;


}
