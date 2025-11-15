package com.itflower.aiplatform.core.handler;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.itflower.aiplatform.ai.model.message.*;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.enums.MessageTypeEnum;
import com.itflower.aiplatform.model.vo.LoginUserVO;
import com.itflower.aiplatform.service.ChatHistoryService;
import io.lettuce.core.StreamMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

/**
 * json 流处理器
 */
@Slf4j
public class JsonStreamHandler {

    @Resource
    ChatHistoryService chatHistoryService;

    /**
     * 对 json 流进行处理，存入对话历史记录
     *
     * @param originFlux 原始流
     * @param appId      应用 id
     * @param loginUser  登录用户
     * @return 处理后的流
     */
    public Flux<String> doHandle(
            Flux<String> originFlux,
            Long appId, User loginUser
    ) {

        StringBuilder chatMessageBuilder = new StringBuilder();
        Set<String> seenToolId = new HashSet<>();
        return originFlux.map(chunk -> {
            String trans = handleJsonStr(chunk, seenToolId);
            chatMessageBuilder.append(trans);
            return trans;
        }).doOnComplete(() -> {
            String completed = chatMessageBuilder.toString();
            chatHistoryService.addChatHistory(appId, loginUser.getId(), completed, MessageTypeEnum.AI_MESSAGE.getValue());
        }).doOnError(throwable -> {
            String msg = String.format("json 流处理失败，原因是: %s", throwable.getMessage());
            log.error("json 流处理失败，原因是: {}", throwable.getMessage());
            chatHistoryService.addChatHistory(appId, loginUser.getId(), msg, MessageTypeEnum.AI_MESSAGE.getValue());
        });
    }

    /**
     * 根据不同消息类型返回不同字符串
     *
     * @param jsonStr json 处理后的消息
     * @return 需要接受的消息
     */
    private String handleJsonStr(String jsonStr, Set<String> seenToolId) {
        StreamingMessage message = JSONUtil.toBean(jsonStr, StreamingMessage.class);
        StreamingMessageTypeEnum typeEnum = StreamingMessageTypeEnum.fromValue(message.getType());
        switch (typeEnum) {
            case AI_RESPONSE -> {
                AiStreamingMessage aiStreamingMessage = JSONUtil.toBean(jsonStr, AiStreamingMessage.class);
                return aiStreamingMessage.getData();
            }
            case TOOL_REQUEST -> {
                ToolRequestMessage tooReq = JSONUtil.toBean(jsonStr, ToolRequestMessage.class);
                String id = tooReq.getId();
                if (!id.isEmpty() && !seenToolId.contains(id)) {
                    seenToolId.add(id);
                    return "\n\n[选择工具] 写入文件\n\n";
                } else {
                    return "";
                }
            }
            case TOOL_EXECUTED -> {
                ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(jsonStr, ToolExecutedMessage.class);
                String arguments = toolExecutedMessage.getArguments();
                JSONObject entries = JSONUtil.parseObj(arguments);
                String relativeFilePath = entries.getStr("relativeFilePath");
                String content = entries.getStr("content");
                String suffix = FileUtil.getSuffix(relativeFilePath);
                String output = String.format("""
                        [工具调用] 写入文件 %s
                        ``` %s
                        %s
                        ```
                        """, relativeFilePath, suffix, content);
                String res = String.format("\n\n%s\n\n", output);
                return res;
            }
            default -> {
                log.warn("不支持的市局格式, {}", typeEnum);
                return "";
            }
        }
    }
}
