package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class CardReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String desr;

    /**
     * 价格
     */
    private BigDecimal balance;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 限制次数，根据type决定是否启用
     */
    private Integer countLimit;

    /**
     * 限制天数，根据type决定是否启用
     */
    private Integer dayLimit;

    /**
     * 库存
     */
    private Integer stock;

    private Integer state;




}
