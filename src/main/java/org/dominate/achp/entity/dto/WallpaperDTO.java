package org.dominate.achp.entity.dto;

import com.hwja.tool.utils.RandomUtil;
import com.hwja.tool.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dominate.achp.entity.req.WallpaperReq;

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
@ToString
public class WallpaperDTO implements Serializable {

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

    public WallpaperDTO() {
        this.code = RandomUtil.createUniqueCode(8);
        this.seq = 0;
        this.title = "";
        this.content = "";
        this.imgSrc = "";
        this.imgInfo = "";
    }

    public WallpaperDTO(WallpaperReq wallpaperReq) {
        if (StringUtil.isEmpty(wallpaperReq.getCode())) {
            this.code = RandomUtil.createUniqueCode(8);
        } else {
            this.code = wallpaperReq.getCode();
        }
        this.seq = wallpaperReq.getSeq();
        this.title = wallpaperReq.getTitle();
        this.content = wallpaperReq.getContent();
        this.imgSrc = wallpaperReq.getImgSrc();
        this.imgInfo = wallpaperReq.getImgInfo();
    }
}


