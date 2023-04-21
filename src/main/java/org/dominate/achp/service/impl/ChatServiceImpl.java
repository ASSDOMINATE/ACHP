package org.dominate.achp.service.impl;

import com.hwja.tool.utils.StringUtil;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.helper.ChatGptHelper;
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
        // 启用线程池接收GPT返回结果
        CompletableFuture.runAsync(() -> {
            String apiKey = baseKeyService.getBestApiKey();
            // 1.开始标记
            try {
                ChatMessage[] messages = ChatGptHelper.createStartMessages(chat);
                for (ChatMessage message : messages) {
                    sseEmitter.send(message);
                }
            } catch (IOException e) {
                log.error("ChatService startChat send start message error ", e);
            }
            List<Integer> contentIdList = chatRecordService.getGroupContentIdList(chat.getChatGroupId(), PageReq.defaultPage());
            List<ContentDTO> contentList = chatContentService.list(contentIdList);
            if (0 != chat.getSceneId()) {
                String system = chatSceneService.getSystem(chat.getSceneId());
                if (StringUtil.isNotEmpty(system)) {
                    chat.setSystem(system);
                }
            }
            try {
                // 2.通过GPT流式传输发送SSE
                ReplyDTO reply = ChatGptHelper.send(chat, contentList, sseEmitter, apiKey);
                // 3.保存结果到数据库
                int contentId = recordContent(chat, reply.getReply());
                // 4.发送本次ID到消息中
                ChatMessage contentIdMessage = ChatGptHelper.createMessage(String.valueOf(contentId), ChatRoleType.CONTENT_CODE);
                sseEmitter.send(contentIdMessage);
            } catch (IOException e) {
                log.error("ChatService startChat send over message error ", e);
            } finally {
                sseEmitter.complete();
            }
        }, commonExecutor);
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
        return content.getId();
    }
}
