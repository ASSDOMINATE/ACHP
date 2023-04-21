package org.dominate.achp.entity.dto;

import com.hwja.tool.utils.StringUtil;
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

    private Integer maxResultTokens;

    private Double temperature;

    /**
     * 对话句子
     */
    private String sentence;

    /**
     * 系统设定
     */
    private String system;

    /**
     * 场景ID
     */
    private Integer sceneId;

    /**
     * 用户账号
     */
    private Integer accountId;

    public ChatDTO(String chatGroupId, String sentence,int sceneId) {
        this.chatGroupId = chatGroupId;
        this.sentence = sentence;
        this.sceneId = sceneId;
        this.modelId = ChatGptHelper.DEFAULT_MODEL_ID;
        this.system = StringUtil.EMPTY;
        this.accountId = 0;
    }
}
