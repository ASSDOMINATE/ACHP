package org.dominate.achp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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
public class UserPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer parentId;

    private Integer platformId;

    /**
     * 权限类型
     */
    private Integer permissionType;

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

    private Date updateTime;

    private Date createTime;

    private Boolean isDel;


}
