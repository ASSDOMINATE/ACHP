package org.dominate.achp.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 支付请求参数
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付卡密ID
     */
    private Integer cardId;

    /**
     * 支付类型
     */
    @NotNull
    private Integer payType;

    /**
     * 支付编码
     */
    @NotNull
    private String orderCode;

    private String auth;
}
