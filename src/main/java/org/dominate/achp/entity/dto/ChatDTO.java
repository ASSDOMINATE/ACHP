package org.dominate.achp.entity.dto;

import com.hwja.tool.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.req.PreSendReq;

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

    public ChatDTO(){}

    public ChatDTO(String chatGroupId, String sentence, int sceneId) {
        this.chatGroupId = chatGroupId;
        this.sentence = sentence;
        this.sceneId = sceneId;
        this.accountId = 0;
        this.system = StringUtil.EMPTY;
        this.modelId = ChatGptHelper.DEFAULT_MODEL_ID;
        this.maxResultTokens = ChatGptHelper.DEFAULT_TOKENS;
        this.temperature = ChatGptHelper.DEFAULT_TEMPERATURE;
    }

    public ChatDTO(String chatGroupId, String sentence, int sceneId, BaseConfig config) {
        this.chatGroupId = chatGroupId;
        this.sentence = sentence;
        this.sceneId = sceneId;
        this.accountId = 0;
        this.system = config.getSetSystem();
        this.modelId = config.getModelId();
        this.maxResultTokens = config.getMaxResultTokens();
        this.temperature = config.getTemperature().doubleValue();
    }

    public ChatDTO(PreSendReq preSendReq, BaseConfig config) {
        this.chatGroupId = StringUtils.isEmpty(preSendReq.getChatId()) ? StringUtils.EMPTY : preSendReq.getChatId();
        this.sceneId = null == preSendReq.getSceneId() ? 0 : preSendReq.getSceneId();
        this.sentence = preSendReq.getSentence();
        this.accountId = 0;
        this.system = config.getSetSystem();
        this.modelId = config.getModelId();
        this.maxResultTokens = config.getMaxResultTokens();
        this.temperature = config.getTemperature().doubleValue();
    }
}
