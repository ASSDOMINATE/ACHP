package org.dominate.achp.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class ResumeAppleReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付卡密ID
     */
    private Integer cardId;

    /**
     * 支付编码
     */
    private String orderCode;

}
