package org.dominate.achp.common.helper;

import com.hwja.tool.utils.StringUtil;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.enums.GptModelType;
import org.dominate.achp.common.utils.ChatTokenUtil;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.ReplyDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.*;

/**
 * ChatGPT 工具
 *
 * @author dominate
 * @since 2023-04-03
 */
@Slf4j
public final class ChatGptHelper {

    public static final String DEFAULT_MODEL_ID = "gpt-3.5-turbo";
    private static final String DEFAULT_API_KEY = "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";
    private static final int ANSWER_LIMIT = 1;
    private static final int FIRST_ANSWER_INDEX = 0;
    private static final int MAX_TOKENS = 50;

    private static final Duration DEFAULT_DURATION = Duration.ofSeconds(55);

    static {
        Locale.setDefault(Locale.CHINA);
    }


    public static List<Model> modelList() {
        return modelList(DEFAULT_API_KEY);
    }

    public static List<Model> modelList(String apiKey) {
        OpenAiService service = new OpenAiService(apiKey, DEFAULT_DURATION);
        return service.listModels();
    }

    /**
     * 发送消息，回复到 sseEmitter
     *
     * @param sentence   用户发起的话
     * @param sseEmitter 本次客户端的请求流
     */
    public static ReplyDTO send(String sentence, SseEmitter sseEmitter) {
        return send(sentence, Collections.emptyList(), sseEmitter, DEFAULT_MODEL_ID, DEFAULT_API_KEY);
    }

    /**
     * 发送消息，回复到 sseEmitter
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @param sseEmitter  本次客户端的请求流
     * @return ReplyDTO 回复
     */
    public static ReplyDTO send(String sentence, List<ContentDTO> contentList, SseEmitter sseEmitter) {
        return send(sentence, contentList, sseEmitter, DEFAULT_MODEL_ID, DEFAULT_API_KEY);
    }

    /**
     * 发送消息，回复到 sseEmitter
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @param modelId     模型ID
     * @param sseEmitter  本次客户端的请求流
     * @return ReplyDTO 回复
     */
    public static ReplyDTO send(String sentence, List<ContentDTO> contentList, SseEmitter sseEmitter, String modelId) {
        return send(sentence, contentList, sseEmitter, modelId, DEFAULT_API_KEY);
    }

    /**
     * 发送消息，回复到 sseEmitter
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @param sseEmitter  本次客户端的请求流
     * @param modelId     模型ID
     * @param apiKey      OpenAi ApiKey
     * @return ReplyDTO 回复
     */
    public static ReplyDTO send(String sentence, List<ContentDTO> contentList, SseEmitter sseEmitter, String modelId, String apiKey) {
        OpenAiService service = new OpenAiService(apiKey, DEFAULT_DURATION);
        ChatCompletionRequest request = createChatRequest(sentence, modelId, parseMessages(contentList, modelId), true);
        StringBuilder reply = new StringBuilder();
        // SSE 关闭
        sseEmitter.onCompletion(service::shutdownExecutor);
        service.streamChatCompletion(request).doOnError(Throwable::printStackTrace).blockingForEach((result) -> {
            ChatMessage message = result.getChoices().get(FIRST_ANSWER_INDEX).getMessage();
            if (StringUtil.isEmpty(message.getContent())) {
                return;
            }
            if (StringUtil.isEmpty(message.getRole())) {
                message.setRole(ChatRoleType.AI.getRole());
            }
            try {
                sseEmitter.send(message);
            } catch (Exception e) {
                log.info("SSE closed , so shutdown stream", e);
                service.shutdownExecutor();
            }
            reply.append(message.getContent());
        });
        service.shutdownExecutor();
        return new ReplyDTO(modelId, sentence, reply.toString());
    }


    /**
     * 发送消息
     *
     * @param sentence 用户发起的话
     * @return AI回复
     */
    public static ReplyDTO send(String sentence) {
        return send(sentence, Collections.emptyList(), DEFAULT_MODEL_ID, DEFAULT_API_KEY);
    }

    /**
     * 发送消息
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @return AI回复
     */
    public static ReplyDTO send(String sentence, List<ContentDTO> contentList) {
        return send(sentence, contentList, DEFAULT_MODEL_ID, DEFAULT_API_KEY);
    }

    /**
     * 发送消息
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @param modelId     模型ID
     * @return AI回复
     */
    public static ReplyDTO send(String sentence, List<ContentDTO> contentList, String modelId) {
        return send(sentence, contentList, modelId, DEFAULT_API_KEY);
    }

    /**
     * 发送消息
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @param modelId     模型ID
     * @param apiKey      OpenAi ApiKey
     * @return AI回复
     */
    public static ReplyDTO send(String sentence, List<ContentDTO> contentList, String modelId, String apiKey) {
        OpenAiService service = new OpenAiService(apiKey, DEFAULT_DURATION);
        ChatCompletionRequest request = createChatRequest(sentence, modelId, parseMessages(contentList, modelId), false);
        ChatCompletionResult result = service.createChatCompletion(request);
        String content = result.getChoices().get(FIRST_ANSWER_INDEX).getMessage().getContent();
        service.shutdownExecutor();
        return new ReplyDTO(modelId, sentence, content);
    }

    /**
     * prompt: 指定与Chatbot进行交互的初始提示。这可以是一个问题、一句话或一段文本。
     * temperature: 控制生成的响应的创造性和多样性。较高的温度会导致更多的随机性和创造性，但可能会降低响应的准确性。默认值为0.7，建议在0.1到1之间进行调整。
     * max_tokens: 指定生成的响应的最大标记数。标记是指模型生成的单词、标点符号和其他符号。默认值为50，建议在10到2048之间进行调整。
     * stop: 指定生成响应的条件。当模型生成的响应包含指定的字符串时，模型将停止生成响应。默认情况下，模型会在生成50个标记后停止。您可以使用stop参数来自定义停止条件。
     * n: 指定生成响应的数量。默认值为1，表示生成一个响应。如果您需要生成多个响应，可以将n设置为所需的数量。
     * presence_penalty: 控制生成的响应中是否包含与初始提示不相关的主题。较高的存在惩罚值会导致生成的响应更加相关，但可能会降低响应的创造性。默认值为0，建议在0到1之间进行调整。
     * frequency_penalty: 控制生成的响应中是否包含与初始提示重复的主题。较高的频率惩罚值会导致生成的响应更加独特，但可能会降低响应的相关性。默认值为0，建议在0到1之间进行调整。
     * best_of: 指定生成响应的数量，并返回其中最佳的响应。默认值为1，表示返回一个响应。如果您需要从多个响应中选择最佳响应，可以将best_of设置为所需的数量。
     * max_turns: 指定对话的最大轮数。默认值为10，建议在1到20之间进行调整。
     * max_history: 指定对话历史记录的最大长度。默认值为2，表示只保留最近的两个对话回合。如果您需要保留更多的对话历史记录，可以将max_history设置为所需的长度。
     *
     * @param sentence 提问的句子
     * @param modelId  所选的语言模型
     * @return 会话请求
     */
    private static CompletionRequest createRequest(String sentence, String modelId, boolean useStream) {
        return CompletionRequest.builder().model(modelId).maxTokens(MAX_TOKENS).echo(true).prompt(sentence).n(ANSWER_LIMIT).stream(useStream).build();
    }

    private static ChatCompletionRequest createChatRequest(String sentence, String modelId, List<ChatMessage> messageList, boolean useStream) {
        if (CollectionUtils.isEmpty(messageList)) {
            messageList = new ArrayList<>();
        }
        messageList.add(createMessage(sentence, true));
        return ChatCompletionRequest.builder().model(modelId).messages(messageList).n(ANSWER_LIMIT).stream(useStream).build();
    }

    private static ChatCompletionRequest createChatRequest(String sentence, String modelId, boolean useStream) {
        return createChatRequest(sentence, modelId, Collections.emptyList(), useStream);
    }

    private static List<ChatMessage> parseMessages(List<ContentDTO> contentList, String modelId) {
        if (CollectionUtils.isEmpty(contentList)) {
            return Collections.emptyList();
        }
        List<ChatMessage> messageList = new ArrayList<>(contentList.size() * 2);
        for (ContentDTO content : contentList) {
            messageList.add(createMessage(content.getSentence(), true));
            messageList.add(createMessage(content.getReply(), false));
        }
        // Tokens 限制检查
        int tokens = ChatTokenUtil.tokens(modelId, messageList);
        int limitTokens = GptModelType.getModelType(modelId).getTokenLimit();
        if (limitTokens >= tokens) {
            return messageList;
        }
        return filter(messageList, modelId, tokens - limitTokens);
    }

    private static List<ChatMessage> filter(List<ChatMessage> messageList, String modelId, int deleteTokens) {
        Iterator<ChatMessage> iterator = messageList.listIterator();
        int totalTokens = 0;
        while (iterator.hasNext()) {
            ChatMessage message = iterator.next();
            totalTokens += ChatTokenUtil.token(modelId, message);
            iterator.remove();
            if (totalTokens >= deleteTokens) {
                return messageList;
            }
        }
        return messageList;
    }


    public static ChatMessage createMessage(String content, boolean forUser) {
        return createMessage(content, forUser ? ChatRoleType.USER : ChatRoleType.AI);
    }

    public static ChatMessage createMessage(String content, ChatRoleType roleEnum) {
        ChatMessage message = new ChatMessage();
        message.setContent(content);
        message.setRole(roleEnum.getRole());
        return message;
    }

    public static ChatMessage[] createStartMessages(ChatDTO chatDTO) {
        ChatMessage signMessage = createMessage(chatDTO.getChatGroupId(), ChatRoleType.CHAT_SIGN);
        ChatMessage title = createMessage(extractTitle(chatDTO.getSentence()), ChatRoleType.TITLE);
        return new ChatMessage[]{signMessage, title};
    }

    public static String extractTitle(String sentence) {
        final int maxTitleLength = 10;
        if (sentence.length() < maxTitleLength) {
            return sentence;
        }
        return sentence.substring(0, maxTitleLength);
    }


}
