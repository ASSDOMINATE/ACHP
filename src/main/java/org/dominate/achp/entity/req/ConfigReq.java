package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 基础配置
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class ConfigReq implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 每日请求次数限制，0 不启用
     */
    private Integer dailyRequestLimit;

    /**
     * 多少秒频率限制，0 不启用
     */
    private Integer freqSecondLimit;

    /**
     * 对话模型ID
     */
    private String modelId;

    private Integer maxResultTokens;

    private BigDecimal temperature;
}
