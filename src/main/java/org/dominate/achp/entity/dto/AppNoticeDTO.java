package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * APP 通知
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
public class AppNoticeDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 问候语
     */
    private String greetings;

    /**
     * 通知列表
     */
    private List<AppRemindDTO> remindList;


    public AppNoticeDTO() {
        this.greetings = "";
        this.remindList = new ArrayList<>();
    }


}


