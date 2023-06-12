package org.dominate.achp.entity.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 统计场景
 * </p>
 *
 * @author dominate
 * @since 2023-04-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class StatisticSceneDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer addReadCount;

    private Integer addChatCount;

    private Integer addSendCount;

    public StatisticSceneDTO() {
        this.addReadCount = 0;
        this.addChatCount = 0;
        this.addSendCount = 0;
    }

    public StatisticSceneDTO(Integer id) {
        this.id = id;
        this.addReadCount = 0;
        this.addChatCount = 0;
        this.addSendCount = 0;
    }

    public void addReadCount(){
        this.addReadCount += 1;
    }

    public void addSendCount(){
        this.addSendCount += 1;
    }

    public void addChatCount(){
        this.addChatCount += 1;
    }


}
