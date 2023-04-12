package org.dominate.achp.sys;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SSE Emitter 包装ResponseHeader后的对象
 *
 * @author dominate
 * @since 2023-04-03
 */
public class ChatSseEmitter extends SseEmitter {

    private static final MediaType TEXT_PLAIN;

    public ChatSseEmitter(Long time) {
        super(time);
    }

    @Override
    protected void extendResponse(ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);
        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType(new MediaType(MediaType.TEXT_EVENT_STREAM, StandardCharsets.UTF_8));
    }

    public static SseEmitter.SseEventBuilder event() {
        return new ChatSseEmitter.SseEventBuilderImpl();
    }

    static {
        TEXT_PLAIN = new MediaType("text", "plain", StandardCharsets.UTF_8);
    }

    private static class SseEventBuilderImpl implements SseEmitter.SseEventBuilder {
        private final Set<DataWithMediaType> dataToSend;
        @Nullable
        private StringBuilder sb;

        private SseEventBuilderImpl() {
            this.dataToSend = new LinkedHashSet<>(4);
        }

        @Override
        public SseEmitter.SseEventBuilder id(String id) {
            this.append("id:").append(id).append('\n');
            return this;
        }

        @Override
        public SseEmitter.SseEventBuilder name(String name) {
            this.append("event:").append(name).append('\n');
            return this;
        }

        @Override
        public SseEmitter.SseEventBuilder reconnectTime(long reconnectTimeMillis) {
            this.append("retry:").append(String.valueOf(reconnectTimeMillis)).append('\n');
            return this;
        }

        @Override
        public SseEmitter.SseEventBuilder comment(String comment) {
            this.append(':').append(comment).append('\n');
            return this;
        }

        @Override
        public SseEmitter.SseEventBuilder data(Object object) {
            return this.data(object, null);
        }

        /**
         * 提供非SSE协议的data返回结果，去除了开头的 data: 及结尾的 /n
         *
         * @param object 发送对象
         * @param mediaType 数据类型
         * @return 事件构建
         */
        @Override
        public SseEmitter.SseEventBuilder data(Object object, @Nullable MediaType mediaType) {
            this.dataToSend.add(new DataWithMediaType(object, mediaType));
            return this;
        }

        ChatSseEmitter.SseEventBuilderImpl append(String text) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }

            this.sb.append(text);
            return this;
        }

        ChatSseEmitter.SseEventBuilderImpl append(char ch) {
            if (this.sb == null) {
                this.sb = new StringBuilder();
            }

            this.sb.append(ch);
            return this;
        }

        @Override
        public Set<DataWithMediaType> build() {
            if (!StringUtils.hasLength(this.sb) && this.dataToSend.isEmpty()) {
                return Collections.emptySet();
            } else {
                this.append('\n');
                this.saveAppendedText();
                return this.dataToSend;
            }
        }

        private void saveAppendedText() {
            if (this.sb != null) {
                this.dataToSend.add(new DataWithMediaType(this.sb.toString(), ChatSseEmitter.TEXT_PLAIN));
                this.sb = null;
            }

        }
    }
}
