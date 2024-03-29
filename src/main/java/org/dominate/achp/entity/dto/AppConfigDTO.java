package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * APP配置信息
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class AppConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本号，用 & 拼接平台在开头
     */
    private String version;

    /**
     * 是否开启兑换
     */
    private Boolean exchange;

    /**
     * 是否需要更新
     */
    private Boolean needUpdate;

    /**
     * 是否强制更新
     */
    private Boolean mustUpdate;

    /**
     * 更新信息
     */
    private String updateInfo;

    /**
     * 更新地址
     */
    private String updateUrl;


    public AppConfigDTO() {
        this.version = "";
        this.needUpdate = false;
        this.mustUpdate = false;
        this.updateInfo = "";
        this.updateUrl = "";
        this.exchange = false;
    }

    public AppConfigDTO(String version) {
        this.version = version;
        this.needUpdate = false;
        this.mustUpdate = false;
        this.updateInfo = "";
        this.updateUrl = "";
        this.exchange = false;
    }
}
