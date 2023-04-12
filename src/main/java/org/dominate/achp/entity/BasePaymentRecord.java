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
 * 支付记录
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("base_payment_record")
public class BasePaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号ID
     */
    @TableField("account_id")
    private Integer accountId;

    /**
     * 支付类型 支付宝/微信
     */
    @TableField("type")
    private Integer type;

    /**
     * 支付金额，负数为退款
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 订单号
     */
    @TableField("order_code")
    private String orderCode;

    /**
     * 三方支付唯一编码
     */
    @TableField("party_code")
    private String partyCode;

    /**
     * 支付备注
     */
    @TableField("comment")
    private String comment;

    /**
     * 支付消息，由系统生成
     */
    @TableField("message")
    private String message;

    @TableField("create_time")
    private Date createTime;


}
