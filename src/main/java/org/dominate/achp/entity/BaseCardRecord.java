package org.dominate.achp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 付费卡密记录
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("base_card_record")
public class BaseCardRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 卡密ID
     */
    @TableField("card_id")
    private Integer cardId;

    @TableField("card_name")
    private String cardName;

    /**
     * 所属用户ID，0代表未绑定用户
     */
    @TableField("account_id")
    private Integer accountId;

    /**
     * 卡密兑换Key
     */
    @TableField("exchange_key")
    private String exchangeKey;

    @TableField("card_type")
    private Integer cardType;

    @TableField("remain_count")
    private Integer remainCount;

    @TableField("request_count")
    private Integer requestCount;

    @TableField("start_time")
    private Date startTime;

    @TableField("expire_time")
    private Date expireTime;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("state")
    private Integer state;


}
