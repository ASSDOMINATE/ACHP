package org.dominate.achp.entity.dto;

import com.hwja.tool.utils.RandomUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * APP 提醒
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class AppRemindDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知编码
     */
    private String code;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否为一次性通知
     */
    private Boolean isDisposable;


    public AppRemindDTO() {
        this.code = RandomUtil.createUniqueCode(8);
        this.title = "";
        this.content = "";
        this.isDisposable = true;
    }

    public AppRemindDTO(String title, String content, Boolean isDisposable) {
        this.code = RandomUtil.createUniqueCode(8);
        this.title = title;
        this.content = content;
        this.isDisposable = isDisposable;
    }

}


