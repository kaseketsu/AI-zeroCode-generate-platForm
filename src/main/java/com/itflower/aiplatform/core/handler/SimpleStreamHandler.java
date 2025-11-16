package com.itflower.aiplatform.core.handler;

import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.enums.MessageTypeEnum;
import com.itflower.aiplatform.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class SimpleStreamHandler {

    @Resource
    ChatHistoryService chatHistoryService;

    public Flux<String> doHandle(
            Flux<String> originFlux,
            Long appId, User loginUser
    ) {
        StringBuilder chatHistoryBuilder = new StringBuilder();
        return originFlux.doOnNext(chatHistoryBuilder::append)
                .doOnComplete(() -> {
                    String history = chatHistoryBuilder.toString();
                    chatHistoryService.addChatHistory(appId, loginUser.getId(), history, MessageTypeEnum.AI_MESSAGE.getValue());
                }).doOnError(throwable -> {
                    log.error("普通流处理失败，原因为: {}", throwable.getMessage());
                    String msg = String.format("普通流处理失败，原因为: %s", throwable.getMessage());
                    chatHistoryService.addChatHistory(appId, loginUser.getId(), msg, MessageTypeEnum.AI_MESSAGE.getValue());
                });
    }
}
