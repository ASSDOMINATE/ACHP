package org.dominate.achp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 付费卡密
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("base_card")
public class BaseCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 描述
     */
    @TableField("desr")
    private String desr;

    /**
     * 价格
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 原始价格
     */
    @TableField("org_balance")
    private BigDecimal orgBalance;

    /**
     * 购买类型
     */
    @TableField("buy_type")
    private Integer buyType;

    /**
     * 标签信息
     */
    @TableField("tag")
    private String tag;

    /**
     * 产品编码
     */
    @TableField("product_code")
    private String productCode;

    /**
     * 类型
     */
    @TableField("type")
    private Integer type;

    /**
     * 限制次数，根据type决定是否启用
     */
    @TableField("count_limit")
    private Integer countLimit;

    /**
     * 限制天数，根据type决定是否启用
     */
    @TableField("day_limit")
    private Integer dayLimit;

    /**
     * 库存
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 排序数
     */
    @TableField("seq")
    private Integer seq;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("state")
    private Integer state;

    @TableField("create_by")
    private Integer createBy;

    @TableField("update_by")
    private Integer updateBy;


}
