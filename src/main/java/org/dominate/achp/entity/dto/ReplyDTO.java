package org.dominate.achp.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 回复对象
 *
 * @author dominate
 * @since 2023-04-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReplyDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private String modelId;

    private String sentence;

    private String reply;

    public ReplyDTO(String modelId, String sentence, String reply) {
        this.modelId = modelId;
        this.sentence = sentence;
        this.reply = reply;
    }
}
