package com.itflower.aiplatform.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiRoutingServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Bean
    public AiRoutingService aiRoutingService() {
        return AiServices.builder(AiRoutingService.class)
                .chatModel(chatModel)
                .build();
    }
}
