package com.itflower.aiplatform.ai.model.message;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * streamingMessage 枚举类
 */
@Getter
public enum StreamingMessageTypeEnum {

    AI_RESPONSE("ai 响应消息", "ai_response"),
    TOOL_REQUEST("工具请求信息", "tool_request"),
    TOOL_EXECUTED("工具结束消息", "tool_executed"),;

    private final String description;

    private final String value;

    StreamingMessageTypeEnum(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public StreamingMessageTypeEnum fromValue(String value) {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        for (StreamingMessageTypeEnum type : StreamingMessageTypeEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
