package com.itflower.aiplatform.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.itflower.aiplatform.ai.tools.FileWriteTool;
import com.itflower.aiplatform.ai.tools.ToolManager;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import com.itflower.aiplatform.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
    private StreamingChatModel openAiStreamingChatModel;

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
     * vue 用推理流式模型
     */
    @Resource
    private StreamingChatModel reasoningStreamingChatModel;

    @Resource
    private ToolManager toolManager;

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

    /**
     * 只根据 appId 获取 AiService
     *
     * @param appId appId
     * @return html / multi 模型
     */
    public AiGeneratorService getAiGeneratorService(Long appId) {
        return serviceCache.get(appId, k -> createAiGeneratorService(appId, GenTypeEnums.HTML));
    }

    /**
     * 根据输出模式获取流式模型
     *
     * @param appId appId
     * @return aiService
     */
    public AiGeneratorService getAiGeneratorService(Long appId, GenTypeEnums genType) {
        return serviceCache.get(appId, k -> createAiGeneratorService(appId, genType));
    }

    /**
     * 获取 AI 生成器服务
     *
     * @param appId 根据 appId 生成 memory, 变相定制 aiService
     * @return AI 生成器服务
     */
    private AiGeneratorService createAiGeneratorService(Long appId, GenTypeEnums genType) {
        MessageWindowChatMemory store = MessageWindowChatMemory.builder()
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(50)
                .id(appId)
                .build();
        // 添加历史聊天记录
        chatHistoryService.loadChatMemory(appId, store, 50);
        // 根据 genType 返回不同的模型
        return switch (genType) {
            case HTML_MULTI, HTML -> AiServices.builder(AiGeneratorService.class)
                    .chatMemory(store)
                    .chatModel(chatModel)
                    .streamingChatModel(openAiStreamingChatModel)
                    .build();
            case VUE_MULTI -> AiServices.builder(AiGeneratorService.class)
                    .chatMemory(store)
                    .chatMemoryProvider(memoryId -> store)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .tools(toolManager.getTools())
                    .hallucinatedToolNameStrategy(req -> ToolExecutionResultMessage.from(
                            req, "Error: there is no tool called " + req.name()
                    ))
                    .build();
            default -> throw new IllegalStateException("不支持的生成类型:  " + genType.getValue());

        };
    }

    /**
     * 获取 AI 生成器服务
     *
     * @return AI 生成器服务
     */
    public AiGeneratorService aiGeneratorService() {
        return createAiGeneratorService(0L, GenTypeEnums.HTML);
    }

    /**
     * 生成 cacheKey
     *
     * @param appId   应用 id
     * @param genType 生成类型枚举
     * @return cacheKey
     */
    private String buildKey(Long appId, GenTypeEnums genType) {
        return appId + "_" + genType.getValue();
    }
}
