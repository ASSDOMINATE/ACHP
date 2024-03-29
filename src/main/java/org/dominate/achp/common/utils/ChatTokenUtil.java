package org.dominate.achp.common.utils;

import com.hwja.tool.utils.StringUtil;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.enums.GptModelType;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Chat Token 计算工具
 *
 * @author dominate
 * @since 2023-04-14
 */
@Slf4j
public class ChatTokenUtil {

    private static final int BASE_NUM_FOR_APPEND = 3;

    /**
     * 通过模型名称, 计算指定字符串的tokens
     *
     * @param modelId 模型ID
     * @param text    要计算的文字
     * @return token 数量
     */
    public static int tokens(@NotNull String modelId, String text) {
        return encode(modelId, text).size();
    }


    /**
     * 通过模型名称计算 messages Tokens
     * 参考官方的处理逻辑：
     * <a href=https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb>https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb</a>
     *
     * @param modelId  模型ID
     * @param messages 消息体列表
     * @return token 数量
     */
    public static int tokens(@NotNull String modelId, @NotNull List<ChatMessage> messages) {
        Encoding encoding = getEncoding(modelId);
        int baseToken = GptModelType.getModelType(modelId).getTokenNum();
        int sum = 0;
        for (ChatMessage msg : messages) {
            sum += token(encoding, msg, baseToken);
        }
        // every reply is primed with <|start|>assistant<|message|>
        sum += BASE_NUM_FOR_APPEND;
        return sum;
    }

    /**
     * 通过模型名称计算 message Tokens
     *
     * @param modelId 模型ID
     * @param message 消息体
     * @return token 数量
     */
    public static int token(@NotNull String modelId, @NotNull ChatMessage message) {
        Encoding encoding = getEncoding(modelId);
        int baseToken = GptModelType.getModelType(modelId).getTokenNum();
        // every reply is primed with <|start|>assistant<|message|>
        return token(encoding, message, baseToken) + BASE_NUM_FOR_APPEND;
    }

    private static int token(Encoding encoding, ChatMessage message, int baseToken) {
        int sum = 0;
        sum += tokens(encoding, message.getRole());
        sum += tokens(encoding, message.getContent());
        // for name
        sum += tokens(encoding, message.getRole());
        // every message follows <|start|>{role/name}\n{content}<|end|>\n
        sum += baseToken;
        return sum;
    }

    public static int baseTokens(@NotNull String modelName, @NotNull List<ChatMessage> messages) {
        Encoding encoding = getEncoding(modelName);
        int tokensPerMessage = 0;
        int tokensPerName = 0;
        //3.5统一处理
        if (modelName.equals("gpt-3.5-turbo-0301") || modelName.equals("gpt-3.5-turbo")) {
            tokensPerMessage = 4;
            tokensPerName = -1;
        }
        //4.0统一处理
        if (modelName.equals("gpt-4") || modelName.equals("gpt-4-0314")) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        }
        int sum = 0;
        for (ChatMessage msg : messages) {
            sum += tokensPerMessage;
            sum += tokens(encoding, msg.getContent());
            sum += tokens(encoding, msg.getRole());
            sum += tokens(encoding, msg.getRole());
            sum += tokensPerName;
        }
        sum += 3;
        return sum;
    }

    private static ModelType getModelTypeByName(String modelId) {
        for (ModelType value : ModelType.values()) {
            if (value.getName().equals(modelId)) {
                return value;
            }
        }
        return ModelType.GPT_3_5_TURBO;
    }

    private static List<Integer> encode(@NotNull Encoding enc, String text) {
        return StringUtil.isBlank(text) ? new ArrayList<>() : enc.encode(text);
    }

    public static int tokens(@NotNull Encoding enc, String text) {
        return encode(enc, text).size();
    }

    private static Encoding getEncoding(@NotNull String modelId) {
        if (ENCODING_MAP.containsKey(modelId)) {
            return ENCODING_MAP.get(modelId);
        }
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        ModelType modelType = getModelTypeByName(modelId);
        Encoding encoding = registry.getEncodingForModel(modelType);
        ENCODING_MAP.put(modelId, encoding);
        return encoding;
    }

    private static final Map<String, Encoding> ENCODING_MAP = new HashMap<>();

    private static List<Integer> encode(@NotNull String modelId, String text) {
        if (StringUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        Encoding enc = getEncoding(modelId);
        if (Objects.isNull(enc)) {
            log.warn("[{}]模型不存在或者暂不支持计算tokens，直接返回tokens==0", modelId);
            return new ArrayList<>();
        }
        return enc.encode(text);
    }
}
