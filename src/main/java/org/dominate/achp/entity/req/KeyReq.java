package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * API-Key配置
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class KeyReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String apiKey;

    private Integer weight;

    private Integer state;


}
