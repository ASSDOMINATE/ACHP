package org.dominate.achp.controller;

import lombok.AllArgsConstructor;
import org.dominate.achp.common.helper.AuthHelper;
import org.dominate.achp.entity.BaseConfig;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.service.CardService;
import org.dominate.achp.service.ChatService;
import org.dominate.achp.service.IBaseConfigService;
import org.dominate.achp.sys.Response;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
        // 用户发消息已达限制
        cardService.checkSendLimit(accountId);
        BaseConfig config = baseConfigService.current();
        ChatDTO chat = new ChatDTO(chatId, sentence, sceneId);
        chat.setAccountId(accountId);
        chat.setModelId(config.getModelId());
        // 记录请求次数
        cardService.addUserRequestRecord(accountId);
        return chatService.startChat(chat);
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
}
