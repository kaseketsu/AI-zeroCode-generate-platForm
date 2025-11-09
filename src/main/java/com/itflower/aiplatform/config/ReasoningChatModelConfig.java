package com.itflower.aiplatform.config;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.SystemMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
@Slf4j
@Data
public class ReasoningChatModelConfig {

    private String baseUrl;

    private String apiKey;

    /**
     * 流式推理模型
     *
     * @return 流式推理
     */
    @Bean
    public StreamingChatModel reasoningStreamingChatModel() {
        // 测试用
        final String modelName = "deepseek-chat";
        final int maxTokens = 8192;
        // 生产用
        // final String modelName = "deepseek-reasoner";
        // final int maxTokens = 32768;
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .maxTokens(maxTokens)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}
