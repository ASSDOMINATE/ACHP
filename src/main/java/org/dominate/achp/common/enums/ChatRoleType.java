package org.dominate.achp.common.enums;

import com.theokanning.openai.completion.chat.ChatMessageRole;

/**
 * ChatMessage 角色枚举
 *
 * @author dominate
 * @since 2023-04-03
 */
public enum ChatRoleType {

    /**
     * 各角色标记字符串
     */
    USER(ChatMessageRole.USER.value(), true),
    SYS(ChatMessageRole.SYSTEM.value(), true),
    AI(ChatMessageRole.ASSISTANT.value(), true),

    TITLE("title", false),
    CHAT_SIGN("sign", false),
    CONTENT_CODE("code", false),
    ;

    private final String role;
    private final boolean forChatGpt;

    ChatRoleType(String role, boolean forChatGpt) {
        this.role = role;
        this.forChatGpt = forChatGpt;
    }

    public String getRole() {
        return role;
    }

    public boolean isForChatGpt() {
        return forChatGpt;
    }
}
