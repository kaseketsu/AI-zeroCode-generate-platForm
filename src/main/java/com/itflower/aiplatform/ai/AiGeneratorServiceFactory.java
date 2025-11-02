package com.itflower.aiplatform.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.itflower.aiplatform.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI 生成器服务工厂
 */
@Configuration
@Slf4j
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
     * redis 记忆仓库
     */
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    /**
     * 对话历史相关业务
     */
    @Resource
    private ChatHistoryService chatHistoryService;

    /**
     * caffeine 本地缓存
     */
    private final Cache<Long, AiGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 实例已被移除，appId: {}，原因: {}", key, cause);
            })
            .build();

    public AiGeneratorService getAiGeneratorService(Long appId) {
        return serviceCache.get(appId, this::createAiGeneratorService);
    }

    /**
     * 获取 AI 生成器服务
     *
     * @param appId 根据 appId 生成 memory, 变相定制 aiService
     * @return AI 生成器服务
     */
    private AiGeneratorService createAiGeneratorService(Long appId) {
        MessageWindowChatMemory store = MessageWindowChatMemory.builder()
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(50)
                .id(appId)
                .build();
        // 添加历史聊天记录
        chatHistoryService.loadChatMemory(appId, store, 20);
        return AiServices.builder(AiGeneratorService.class)
                .chatMemory(store)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }

    /**
     * 获取 AI 生成器服务
     *
     * @return AI 生成器服务
     */
    public AiGeneratorService aiGeneratorService() {
        return createAiGeneratorService(0L);
    }
}
