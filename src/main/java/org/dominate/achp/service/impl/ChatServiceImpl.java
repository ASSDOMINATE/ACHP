package org.dominate.achp.service.impl;

import com.hwja.tool.utils.StringUtil;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.common.utils.FreqUtil;
import org.dominate.achp.common.utils.UniqueCodeUtil;
import org.dominate.achp.entity.ChatContent;
import org.dominate.achp.entity.ChatRecord;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.ReplyDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.service.*;
import org.dominate.achp.sys.ChatSseEmitter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


/**
 * Chat 服务类实现
 *
 * @author dominate
 * @since 2023-04-03
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    @Resource(name = "commonExecutor")
    private Executor commonExecutor;

    private final IChatGroupService chatGroupService;
    private final IChatContentService chatContentService;
    private final IChatRecordService chatRecordService;
    private final IChatSceneService chatSceneService;

    private final IBaseKeyService baseKeyService;

    @Override
    public SseEmitter startChat(ChatDTO chat) {
        if (StringUtils.isBlank(chat.getChatGroupId())) {
            chat.setChatGroupId(UniqueCodeUtil.createChatId());
        }
        ChatSseEmitter sseEmitter = new ChatSseEmitter(0L);
        // 启用线程池发送消息
        CompletableFuture.runAsync(() -> send(sseEmitter, chat), commonExecutor);
        return sseEmitter;
    }


    private void send(ChatSseEmitter sseEmitter, ChatDTO chat) {
        // 1.发送 -> 开始标记
        sendStartSign(chat, sseEmitter);
        // 2.获取当前最合适的 Api-Key
        String apiKey = baseKeyService.getBestApiKey();
        // 3.等待 Api-key 的频率
        boolean isAvailableKey = FreqUtil.waitFreqForApiKey(apiKey);
        if (!isAvailableKey) {
            try {
                sseEmitter.send(ChatGptHelper.createMessage("当前请求过多，已达服务器限制，请稍后再试", false));
            } catch (IOException e) {
                log.error("Client SSE is closed ", e);
            } finally {
                FreqUtil.releaseApiKey(apiKey);
                sseEmitter.complete();
            }
            return;
        }
        // 4.读取上下文
        List<ContentDTO> contentList = loadGroupContentList(chat.getChatGroupId());
        // 5.设置场景的系统角色
        setSceneSystem(chat);
        try {
            // 6.发送 -> ChatGPT返回消息，时间较长
            ReplyDTO reply = ChatGptHelper.send(sseEmitter, contentList, apiKey, chat);
            // 7.保存结果到数据库
            int contentId = recordContent(chat, reply.getReply());
            // 8.发送 -> 本次会话ID
            sseEmitter.send(ChatGptHelper.createMessage(String.valueOf(contentId), ChatRoleType.CONTENT_CODE));
        } catch (IOException e) {
            log.error("ChatService.startChat send error ", e);
            try {
                // 把ChatGPT的报错消息发送到前端
                sseEmitter.send(ChatGptHelper.createMessage(e.getMessage(), false));
            } catch (IOException ex) {
                log.error("Client SSE is closed ", e);
            }
        } finally {
            // 9.释放 ApiKey 的频率
            FreqUtil.releaseApiKey(apiKey);
            sseEmitter.complete();
        }
    }

    @Override
    public String question(ChatDTO chatDTO) {
        return ChatGptHelper.send(chatDTO.getSentence(), Collections.emptyList()).getReply();
    }

    @Override
    public int recordContent(ChatDTO chat, String reply) {
        // 1.save content
        int lastContentId = chatRecordService.getLastContentId(chat.getChatGroupId());
        ChatContent content = new ChatContent(chat, reply, lastContentId);
        if (!chatContentService.save(content)) {
            log.error("Save chat content error " + chat);
            return 0;
        }
        // 2.check group
        if (!chatGroupService.checkGroup(chat, chat.getAccountId())) {
            log.error("Save chat group error " + chat);
        }
        // 3.save record , link user - scene - group - content
        ChatRecord record = new ChatRecord()
                .setAccountId(chat.getAccountId())
                .setSceneId(chat.getSceneId())
                .setContentId(content.getId())
                .setGroupId(chat.getChatGroupId());
        if (!chatRecordService.save(record)) {
            log.error("Save chat record error " + chat);
        }
        return content.getId();
    }

    private void sendStartSign(ChatDTO chat, ChatSseEmitter sseEmitter) {
        try {
            ChatMessage[] messages = ChatGptHelper.createStartMessages(chat);
            for (ChatMessage message : messages) {
                sseEmitter.send(message);
            }
        } catch (IOException e) {
            log.error("ChatService.startChat send start message error ", e);
        }
    }

    private List<ContentDTO> loadGroupContentList(String groupId) {
        List<Integer> contentIdList = chatRecordService.getGroupContentIdList(groupId, PageReq.chatPage());
        return chatContentService.list(contentIdList);
    }

    private void setSceneSystem(ChatDTO chat) {
        if (0 == chat.getSceneId()) {
            return;
        }
        String system = chatSceneService.getSystem(chat.getSceneId());
        if (StringUtil.isNotEmpty(system)) {
            chat.setSystem(system);
        }
    }
}
