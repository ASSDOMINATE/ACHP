package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 支付对象
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class PayResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private String sysOrderCode;

    private String partyOrderCode;

    private String codeUrl;

    private String sign;



}
