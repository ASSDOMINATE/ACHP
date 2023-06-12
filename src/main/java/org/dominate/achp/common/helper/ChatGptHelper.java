package org.dominate.achp.common.helper;

import com.hwja.tool.utils.HttpUtil;
import com.hwja.tool.utils.JsonUtil;
import com.hwja.tool.utils.StringUtil;
import com.openai.theokanning.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.model.Model;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.enums.ChatFailedType;
import org.dominate.achp.common.enums.ChatRoleType;
import org.dominate.achp.common.enums.ExceptionType;
import org.dominate.achp.common.enums.GptModelType;
import org.dominate.achp.common.utils.ChatTokenUtil;
import org.dominate.achp.entity.dto.ChatDTO;
import org.dominate.achp.entity.dto.ChatGroupDTO;
import org.dominate.achp.entity.dto.ContentDTO;
import org.dominate.achp.entity.dto.ReplyDTO;
import org.dominate.achp.sys.exception.BusinessException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * ChatGPT 工具
 *
 * @author dominate
 * @since 2023-04-03
 */
@Slf4j
public final class ChatGptHelper {

    public static final String DEFAULT_MODEL_ID = "gpt-3.5-turbo-0301";
    public static final double DEFAULT_TEMPERATURE = 0.8;
    public static final int DEFAULT_TOKENS = 100000;

    private static final String DEFAULT_API_KEY = "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";
    private static final int TOKEN_RESERVE = 256;

    private static final int ANSWER_LIMIT = 1;
    private static final int FIRST_ANSWER_INDEX = 0;

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
     * @param sseEmitter  本次客户端的请求流
     * @param contentList 前置对话
     * @param apiKey      apiKey
     * @param chat        会话对象
     * @return AI回复
     */
    public static ReplyDTO send(SseEmitter sseEmitter, List<ContentDTO> contentList, String apiKey, ChatDTO chat) {
        return send(sseEmitter, contentList, apiKey, chat.getSentence(), chat.getSystem(), chat.getModelId(), chat.getMaxResultTokens(), chat.getTemperature());
    }

    /**
     * 发送消息，回复到 sseEmitter
     *
     * @param sseEmitter   本次客户端的请求流
     * @param contentList  会话历史
     * @param sentence     用户发起的话
     * @param system       系统预设角色
     * @param modelId      模型ID
     * @param resultTokens 返回结果限制Tokens
     * @param temperature  返回结果热度设置
     * @param apiKey       本次使用的ApiKey
     * @return ReplyDTO 回复
     */
    public static ReplyDTO send(SseEmitter sseEmitter, List<ContentDTO> contentList, String apiKey, String sentence, String system, String modelId,
                                int resultTokens, Double temperature) {
        OpenAiService service = new OpenAiService(apiKey, DEFAULT_DURATION);
        ChatGroupDTO group = parseMessages(contentList, sentence, system, modelId, resultTokens);
        ChatCompletionRequest request = createChatRequest(modelId, resultTokens, temperature, group.getMessageList(), true);
        StringBuilder reply = new StringBuilder();
        service.streamChatCompletion(request).blockingForEach((result) -> {
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
                log.info("ChatGptHelper.send Client SSE closed , so shutdown Chat-GPT stream , => {}", e.getMessage());
                // 抛异常即可中断 blockingForEach
                throw BusinessException.create(ExceptionType.ERROR, ChatFailedType.CLIENT_CLOSE.getResult());
            }
            reply.append(message.getContent());
        });
        service.shutdownExecutor();
        return new ReplyDTO(modelId, sentence, reply.toString(), group.getSendTokens());
    }

    /**
     * 发送消息
     *
     * @param sentence 用户发起的话
     * @return AI回复
     */
    public static ReplyDTO send(String sentence) {
        return send(sentence, StringUtil.EMPTY, Collections.emptyList(), DEFAULT_MODEL_ID, DEFAULT_TOKENS, DEFAULT_TEMPERATURE, DEFAULT_API_KEY);
    }

    /**
     * 发送消息
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @return AI回复
     */
    public static ReplyDTO send(String sentence, List<ContentDTO> contentList) {
        return send(sentence, StringUtil.EMPTY, contentList, DEFAULT_MODEL_ID, DEFAULT_TOKENS, DEFAULT_TEMPERATURE, DEFAULT_API_KEY);
    }

    /**
     * 发送消息
     *
     * @param sentence    用户发起的话
     * @param contentList 会话历史
     * @return AI回复
     */
    public static ReplyDTO send(String sentence, String system, List<ContentDTO> contentList) {
        return send(sentence, system, contentList, DEFAULT_MODEL_ID, DEFAULT_TOKENS, DEFAULT_TEMPERATURE, DEFAULT_API_KEY);
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
    public static ReplyDTO send(String sentence, String system, List<ContentDTO> contentList, String modelId, int resultTokens, double temperature, String apiKey) {
        OpenAiService service = new OpenAiService(apiKey, DEFAULT_DURATION);
        ChatGroupDTO group = parseMessages(contentList, sentence, system, modelId, resultTokens);
        ChatCompletionRequest request = createChatRequest(modelId, resultTokens, temperature, group.getMessageList(), false);
        ChatCompletionResult result = service.createChatCompletion(request);
        String content = result.getChoices().get(FIRST_ANSWER_INDEX).getMessage().getContent();
        service.shutdownExecutor();
        return new ReplyDTO(modelId, sentence, content, group.getSendTokens());
    }

    /**
     * 生成对话消息对象
     *
     * @param content 内容
     * @param forUser 是否用户发出的
     * @return 对话消息对象
     */
    public static ChatMessage createMessage(String content, boolean forUser) {
        return createMessage(content, forUser ? ChatRoleType.USER : ChatRoleType.AI);
    }

    /**
     * 生成对话消息对象
     *
     * @param content  内容
     * @param roleEnum 消息角色枚举
     * @return 对话消息对象
     */
    public static ChatMessage createMessage(String content, ChatRoleType roleEnum) {
        ChatMessage message = new ChatMessage();
        message.setContent(content);
        message.setRole(roleEnum.getRole());
        return message;
    }

    /**
     * 生成对话消息开头标识
     *
     * @param chatDTO 对话数据
     * @return 消息对象开头数组 长度 2
     */
    public static ChatMessage[] createStartMessages(ChatDTO chatDTO) {
        ChatMessage signMessage = createMessage(chatDTO.getChatGroupId(), ChatRoleType.CHAT_SIGN);
        ChatMessage title = createMessage(extractTitle(chatDTO.getSentence()), ChatRoleType.TITLE);
        return new ChatMessage[]{signMessage, title};
    }

    /**
     * 提取会话标题
     *
     * @param sentence 内容
     * @return 标题
     */
    public static String extractTitle(String sentence) {
        final int maxTitleLength = 10;
        if (sentence.length() < maxTitleLength) {
            return sentence;
        }
        return sentence.substring(0, maxTitleLength);
    }

    /**
     * 解析生成对话消息列表
     * <p>
     * 可能会过滤掉消息中的部分历史对话
     * 消息列表+返回结果的Tokens数量需要小于请求模型的Tokens限制
     *
     * @param contentList  历史对话列表
     * @param sentence     本次提问
     * @param system       系统预设
     * @param modelId      模型ID
     * @param resultTokens 返回结果的Tokens限制
     * @return 对话消息列表
     */
    public static ChatGroupDTO parseMessages(List<ContentDTO> contentList, String sentence, String system, String modelId, int resultTokens) {
        if (CollectionUtils.isEmpty(contentList)) {
            // 无上下文
            List<ChatMessage> messageList = new ArrayList<>(2);
            if (StringUtil.isNotEmpty(system)) {
                // 1.添加系统角色
                messageList.add(createMessage(system, ChatRoleType.SYS));
            }
            // 2.本次提问
            messageList.add(createMessage(sentence, true));
            int tokens = ChatTokenUtil.tokens(modelId, messageList);
            return new ChatGroupDTO(messageList, tokens);
        }
        List<ChatMessage> messageList = new ArrayList<>(contentList.size() * 2);
        if (StringUtil.isNotEmpty(system)) {
            // 1.添加系统角色
            messageList.add(createMessage(system, ChatRoleType.SYS));
        }
        // 2.添加历史对话
        for (ContentDTO content : contentList) {
            messageList.add(createMessage(content.getSentence(), true));
            messageList.add(createMessage(content.getReply(), false));
        }
        // 3.本次提问
        messageList.add(createMessage(sentence, true));
        // 4.Tokens限制检查
        int tokens = ChatTokenUtil.tokens(modelId, messageList);
        int limitTokens = GptModelType.getModelType(modelId).getTokenLimit() - resultTokens - TOKEN_RESERVE;
        if (limitTokens >= tokens) {
            return new ChatGroupDTO(messageList, tokens);
        }
        log.info("需过滤连续对话，最大发送Token {}, 本次请求Token {}", limitTokens, tokens);
        return filter(messageList, modelId, tokens, limitTokens);
    }

    public static String requestWallet(int day, String apiKey) {
        String url = OpenAiService.loadServerUrl() + "/v1/dashboard/billing/usage";
        LocalDate date = LocalDate.now();
        String uri = HttpUtil.createParamUri(new String[]{"start_date", "end_date"},
                new String[]{date.minus(day, ChronoUnit.DAYS).toString(), date.toString()});
        String response = HttpUtil.sendGetSetHeader(url + "?" + uri, createBillingHeader(apiKey));
        String totalUsage = JsonUtil.parseResponseValueForString(response, "total_usage");
        if (StringUtil.isNotEmpty(totalUsage)) {
            String limitDollar = requestLimitDollar(apiKey);
            return day + "天内已使用 " + totalUsage + "美分，当前限制额度 " + limitDollar + "美元";
        }
        return JsonUtil.parseResponseValueForString(response, "error", "message");
    }

    private static String requestLimitDollar(String apiKey) {
        String url = OpenAiService.loadServerUrl() + "/v1/dashboard/billing/subscription";
        String response = HttpUtil.sendGetSetHeader(url, createBillingHeader(apiKey));
        String limitUsd = JsonUtil.parseResponseValueForString(response, "hard_limit_usd");
        if (StringUtil.isNotEmpty(limitUsd)) {
            return limitUsd;
        }
        return JsonUtil.parseResponseValueForString(response, "error", "message");
    }

    private static Map<String, String> createBillingHeader(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        return headers;
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
     * @param modelId 所选的语言模型
     * @return 会话请求
     */
    private static ChatCompletionRequest createChatRequest(String modelId, int resultTokens, double temperature, List<ChatMessage> messageList, boolean useStream) {
        if (CollectionUtils.isEmpty(messageList)) {
            messageList = new ArrayList<>();
        }
        return ChatCompletionRequest.builder()
                .model(modelId)
                .temperature(temperature)
                .maxTokens(resultTokens)
                .messages(messageList)
                .n(ANSWER_LIMIT)
                .stream(useStream)
                .build();
    }

    private static ChatGroupDTO filter(List<ChatMessage> messageList, String modelId, int tokens, int limitTokens) {
        Iterator<ChatMessage> iterator = messageList.listIterator();
        int targetTokens = tokens - limitTokens;
        int deleteTokens = 0;
        log.info("本次连续会话条数 {}", messageList.size());
        while (iterator.hasNext()) {
            ChatMessage message = iterator.next();
            deleteTokens += ChatTokenUtil.token(modelId, message);
            if (ChatRoleType.SYS.getRole().equals(message.getRole())) {
                continue;
            }
            iterator.remove();
            if (deleteTokens >= targetTokens) {
                log.info("需过滤Token {}, 已过滤Token {}, 过滤后会话条数 {} ", targetTokens, deleteTokens, messageList.size());
                if (CollectionUtils.isEmpty(messageList)) {
                    throw BusinessException.create(ExceptionType.ERROR, ChatFailedType.CONTENT_TO_LONG.getResult());
                }
                return new ChatGroupDTO(messageList, tokens - deleteTokens);
            }
        }
        return new ChatGroupDTO(messageList, tokens - deleteTokens);
    }


}
