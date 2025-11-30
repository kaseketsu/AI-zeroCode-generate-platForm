package com.itflower.aiplatform.core.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.itflower.aiplatform.ai.model.message.*;
import com.itflower.aiplatform.ai.tools.BaseTool;
import com.itflower.aiplatform.ai.tools.ToolManager;
import com.itflower.aiplatform.constant.AppConstant;
import com.itflower.aiplatform.core.build.VueProjectBuilder;
import com.itflower.aiplatform.model.entity.User;
import com.itflower.aiplatform.model.enums.MessageTypeEnum;
import com.itflower.aiplatform.service.ChatHistoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * json 流处理器
 */
@Slf4j
@Component
public class JsonStreamHandler {

    @Resource
    ChatHistoryService chatHistoryService;

    @Resource
    VueProjectBuilder vueProjectBuilder;

    @Resource
    private ToolManager toolManager;

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
            vueProjectBuilder.buildProjectAsync(new File(AppConstant.OUT_PUT_PATH + "/vue_project_" + appId));
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
                String name = tooReq.getName();
                String id = tooReq.getId();
                if (!id.isEmpty() && !seenToolId.contains(id)) {
                    seenToolId.add(id);
                    BaseTool tool = toolManager.getTool(name);
                    return tool.getRequestContent();
                } else {
                    return "";
                }
            }
            case TOOL_EXECUTED -> {
                ToolExecutedMessage toolExecutedMessage = JSONUtil.toBean(jsonStr, ToolExecutedMessage.class);
                String name = toolExecutedMessage.getName();
                String arguments = toolExecutedMessage.getArguments();
                JSONObject entries = JSONUtil.parseObj(arguments);
                BaseTool tool = toolManager.getTool(name);
                String toolExecutedContent = tool.getToolExecutedContent(entries);
                return String.format("\n\n%s\n\n", toolExecutedContent);
            }
            default -> {
                log.warn("不支持的市局格式, {}", typeEnum);
                return "";
            }
        }
    }
}
