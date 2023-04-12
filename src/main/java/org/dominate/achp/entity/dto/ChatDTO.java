package org.dominate.achp.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.dominate.achp.common.helper.ChatGptHelper;

import java.io.Serializable;

/**
 * 会话对象
 *
 * @author dominate
 * @since 2023-04-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对话组ID
     */
    private String chatGroupId;

    /**
     * 对话模型ID
     */
    private String modelId;

    /**
     * 对话句子
     */
    private String sentence;

    /**
     * 场景ID
     */
    private int sceneId;

    /**
     * 用户账号
     */
    private int accountId;

    public ChatDTO(String chatGroupId, String sentence,int sceneId) {
        this.chatGroupId = chatGroupId;
        this.sentence = sentence;
        this.sceneId = sceneId;
        this.modelId = ChatGptHelper.DEFAULT_MODEL_ID;
        this.accountId = 0;
    }
}
