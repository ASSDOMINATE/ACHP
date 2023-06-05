package org.dominate.achp.controller;

import com.hwja.tool.utils.RandomUtil;
import lombok.AllArgsConstructor;
import org.dominate.achp.common.cache.ChatCache;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.req.PreSendReq;
import org.dominate.achp.service.CardService;
import org.dominate.achp.service.ChatService;
import org.dominate.achp.service.IBaseConfigService;
import org.dominate.achp.sys.ChatSseEmitter;
import org.dominate.achp.sys.Response;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 对话相关接口
 *
 * @author dominate
 * @since 2023-04-03
 */
@RestController
@RequestMapping("/chat")
@AllArgsConstructor
public class ApiChatController {

    private final ChatService chatService;
    private final CardService cardService;

    private final IBaseConfigService baseConfigService;

    /**
     * 发送对话内容
     * Stream 结果返回
     *
     * @param token    用户标识
     * @param chatId   对话ID，选填
     * @param sceneId  场景ID，选填
     * @param sentence 发送句子
     * @return SseEmitter SSE接收器
     */
    @GetMapping(path = "send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter send(
            @RequestHeader(name = "token", required = false) String token,
            @RequestParam(name = "chat_id", required = false) String chatId,
            @RequestParam(name = "scene_id", required = false, defaultValue = "0") Integer sceneId,
            @RequestParam(name = "sentence") String sentence) {
        int accountId = AuthHelper.parseWithValidForId(token);
        // 用户发消息限制检查
        cardService.checkSendLimit(accountId);
        BaseConfig config = baseConfigService.current();
        ChatDTO chat = new ChatDTO(chatId, sentence, sceneId, config);
        // 记录请求次数
        cardService.addUserRequestRecord(accountId);
        return chatService.startChat(chat);
    }


    @PostMapping(path = "preSend")
    @ResponseBody
    public Response<String> preSend(
            @RequestHeader(name = "token", required = false) String token,
            @Validated @RequestBody PreSendReq preSendReq
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        // 用户发消息限制检查
        cardService.checkSendLimit(accountId);
        BaseConfig config = baseConfigService.current();
        ChatDTO chat = new ChatDTO(preSendReq, config);
        // 记录请求次数
        cardService.addUserRequestRecord(accountId);
        return Response.data(ChatCache.saveChatSendTemp(chat));
    }

    @GetMapping(path = "sendByPre", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendByPre(
            @RequestParam(name = "key") String key
    ) {
        ChatDTO chat = ChatCache.getChatSend(key);
        return chatService.startChat(chat);
    }


    @GetMapping(path = "checkSendLimit")
    @ResponseBody
    public Response<Boolean> checkSendLimit(
            @RequestHeader(name = "token", required = false) String token
    ) {
        int accountId = AuthHelper.parseWithValidForId(token);
        // 用户发消息限制检查
        try {
            cardService.checkSendLimit(accountId);
            return Response.data(true);
        } catch (Exception e) {
            return Response.data(false);
        }
    }


    // 调试用接口

    @GetMapping(path = "question")
    @ResponseBody
    public Response<String> question(
            @RequestParam(name = "chat_id", required = false) String chatId,
            @RequestParam(name = "scene_id", required = false, defaultValue = "0") Integer sceneId,
            @RequestParam(name = "sentence") String sentence) {
        ChatDTO chat = new ChatDTO(chatId, sentence, sceneId);
        return Response.data(chatService.question(chat));
    }

    private static final Map<String, SseEmitter> SSE_CACHE_MAP = new HashMap<>();

    @GetMapping(path = "clearSSE")
    @ResponseBody
    public Response<String> clearSSE() {
        Iterator<Map.Entry<String, SseEmitter>> iterator = SSE_CACHE_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().complete();
            iterator.remove();
        }
        return Response.success();
    }

    @GetMapping(path = "sendSSE")
    @ResponseBody
    public Response<String> sendSSE(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "content", required = false) String content
    ) {
        try {
            SSE_CACHE_MAP.get(key).send(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.success();
    }

    @GetMapping(path = "getSSE", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getSSE() {
        ChatSseEmitter sseEmitter = new ChatSseEmitter(0L);
        String key = RandomUtil.getStringRandom(8);
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            try {
                sseEmitter.send(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 1, TimeUnit.SECONDS);
        SSE_CACHE_MAP.put(key, sseEmitter);
        return sseEmitter;
    }
}
