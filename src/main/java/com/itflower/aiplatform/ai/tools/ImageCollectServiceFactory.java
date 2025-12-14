package com.itflower.aiplatform.ai.tools;

import com.itflower.aiplatform.ai.ImageCollectService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ImageCollectServiceFactory {

    @Resource
    private ChatModel chatModel;

    @Resource
    private ImageFetchTool imageFetchTool;

    @Bean
    public ImageCollectService imageCollectService() {
        return AiServices.builder(ImageCollectService.class)
                .chatModel(chatModel)
                .tools(imageFetchTool)
                .build();
    }
}
