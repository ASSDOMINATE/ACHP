package org.dominate.achp.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号ID
     */
    private Integer accountId;

    /**
     * 用户唯一编码
     */
    private String uniqueCode;

    /**
     * 别名
     */
    private String alias;

    /**
     * 名字 最大长度45
     */
    private String name;

    /**
     * 电话 最大长度45
     */
    private String phone;

    /**
     * 邮箱 最大长度45
     */
    private String email;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 头像地址 最大长度1024
     */
    private String avatar;

    /**
     * 是否在职
     */
    private Integer state;

    /**
     * 身份证号
     */
    private String identity;

    private Boolean isAdmin;

    public UserDTO() {
    }

}
