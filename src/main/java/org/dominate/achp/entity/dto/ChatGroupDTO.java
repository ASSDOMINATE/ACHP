package org.dominate.achp.entity.dto;

import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 回复对象
 *
 * @author dominate
 * @since 2023-04-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatGroupDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ChatMessage>  messageList;

    private int sendTokens;

    public ChatGroupDTO(List<ChatMessage> messageList, int sendTokens) {
        this.messageList = messageList;
        this.sendTokens = sendTokens;
    }
}
