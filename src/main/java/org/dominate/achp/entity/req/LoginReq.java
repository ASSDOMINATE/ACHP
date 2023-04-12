package org.dominate.achp.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 登陆请求参数
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 手机号/邮箱/唯一编码
     */
    @NotNull
    private String sign;

    @NotNull
    private String pwd;

    private Boolean forClient;


}
