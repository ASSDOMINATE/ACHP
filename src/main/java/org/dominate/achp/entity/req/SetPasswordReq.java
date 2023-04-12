package org.dominate.achp.entity.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 设置密码请求参数
 * </p>
 *
 * @author dominate
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SetPasswordReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "新密码不能为空")
    private final String pwd;

    @NotNull(message = "短信验证码不能为空")
    private final String code;


}
