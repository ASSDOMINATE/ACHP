package org.dominate.achp.service.impl;

import com.hwja.tool.utils.StringUtil;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.cache.ChatCache;
import org.dominate.achp.common.enums.ChatFailedType;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.helper.ChatGptHelper;
import org.dominate.achp.common.utils.FreqUtil;
import org.dominate.achp.common.utils.UniqueCodeUtil;
import org.dominate.achp.entity.ChatContent;
import org.dominate.achp.entity.ChatRecord;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.GroupCacheDTO;
import org.dominate.achp.entity.dto.ReplyDTO;
import org.dominate.achp.entity.req.PageReq;
import org.dominate.achp.entity.wrapper.ChatWrapper;
import org.dominate.achp.service.*;
import org.dominate.achp.sys.ChatSseEmitter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;


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
        CompletableFuture.runAsync(() -> send(sseEmitter, chat), Executors.newCachedThreadPool());
        return sseEmitter;
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
        ChatCache.saveChatGroupContentTemp(chat.getChatGroupId(), ChatWrapper.build().entityDTO(content));
        return content.getId();
    }

    private void send(ChatSseEmitter sseEmitter, ChatDTO chat) {
        // 1.发送 -> 开始标记
        sendStartSign(chat, sseEmitter);
        // 2.获取当前最合适的 Api-Key  from DB or Cache
        String apiKey = baseKeyService.getBestApiKey();
        // 3.等待 Api-key 的频率
        if (!FreqUtil.waitFreqForApiKey(apiKey)) {
            // 等待后仍然超过频率限制
            try {
                sseEmitter.send(ChatGptHelper.createMessage(ChatFailedType.MODEL_OVERLOADED.getResult(), false));
            } catch (IOException e) {
                log.error("ChatService.sendFreqLimit Client SSE is closed => {}", e.getMessage());
            } finally {
                sseEmitter.complete();
            }
            return;
        }
        // 4.读取上下文 from DB or Cache
        List<ContentDTO> contentList = loadGroupContentList(chat.getChatGroupId());
        // 5.设置场景的系统角色 from DB
        setSceneSystem(chat);
        int sendToken = 0;
        try {
            // 6.发送 -> ChatGPT返回消息，时间较长
            ReplyDTO reply = ChatGptHelper.send(sseEmitter, contentList, apiKey, chat);
            sendToken = reply.getSendTokens();
            // 7.保存结果到数据库 to DB
            int contentId = recordContent(chat, reply.getReply());
            // 8.发送 -> 本次会话ID
            sseEmitter.send(ChatGptHelper.createMessage(String.valueOf(contentId), ChatRoleType.CONTENT_CODE));
        } catch (Exception e) {
            log.error("ChatService.startChat send error => {}", e.getMessage());
            String errorMessage = ChatFailedType.parseSign(e.getMessage());
            try {
                // 把ChatGPT的报错消息发送到前端
                sseEmitter.send(ChatGptHelper.createMessage(errorMessage, false));
            } catch (IOException ex) {
                log.error("ChatService.sendError Client SSE is closed => {}", errorMessage);
            }
        } finally {
            // 9.增加 ApiKey 的频率
            FreqUtil.addFreqApiKey(apiKey, sendToken);
            sseEmitter.complete();
        }
    }

    private void sendStartSign(ChatDTO chat, ChatSseEmitter sseEmitter) {
        try {
            ChatMessage[] messages = ChatGptHelper.createStartMessages(chat);
            for (ChatMessage message : messages) {
                sseEmitter.send(message);
            }
        } catch (IOException e) {
            log.error("ChatService.sendStartSign Client SSE is closed => {}", e.getMessage());
        }
    }

    private List<ContentDTO> loadGroupContentList(String groupId) {
        GroupCacheDTO groupCache = ChatCache.getChatGroup(groupId);
        if (null != groupCache) {
            return groupCache.getContentList();
        }
        List<Integer> contentIdList = chatRecordService.getGroupContentIdList(groupId, PageReq.chatPage());
        List<ContentDTO> contentList = chatContentService.list(contentIdList);
        ChatCache.saveChatGroupTemp(new GroupCacheDTO(groupId, contentList));
        return contentList;
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
