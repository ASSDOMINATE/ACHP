package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 配置信息
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String version;

    private Boolean exchange;


    public AppConfigDTO(){
    }

    public AppConfigDTO(String version) {
        this.version = version;
        this.exchange = false;
    }
}
