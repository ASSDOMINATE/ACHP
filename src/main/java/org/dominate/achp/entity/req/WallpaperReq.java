package org.dominate.achp.entity.req;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 壁纸对象
 * </p>
 *
 * @author dominate
 * @since 2023-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
public class WallpaperReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer seq;

    /**
     * 唯一编码
     */
    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片地址
     */
    private String imgSrc;

    /**
     * 图片信息 Json
     */
    private String imgInfo;

}


