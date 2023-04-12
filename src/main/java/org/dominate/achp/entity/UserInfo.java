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
 * 用户信息
 * </p>
 *
 * @author dominate
 * @since 2022-01-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号ID
     */
    private Integer accountId;

    /**
     * 唯一编码
     */
    private String uniqueCode;

    /**
     * 身份证号
     */
    private String identity;

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

    private Date createTime;

    private Date updateTime;


}
