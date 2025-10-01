package com.itflower.aiplatform.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 生成器服务工厂
 */
@Configuration
public class AiGeneratorServiceFactory {


    /**
     * 语言模型
     */
    @Resource
    private ChatModel chatModel;

    /**
     * 流式语言模型
     */
    @Resource
    private StreamingChatModel streamingChatModel;

    /**
     * 获取 AI 生成器服务
     *
     * @return AI 生成器服务
     */
    @Bean
    public AiGeneratorService aiGeneratorService() {
        return AiServices.builder(AiGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
