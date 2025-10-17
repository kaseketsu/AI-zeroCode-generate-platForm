package com.itflower.aiplatform.model.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public enum MessageTypeEnum {

    AI_MESSAGE("AI 消息", "ai"),
    USER_MESSAGE("用户消息", "user");

    private final String description;

    private final String value;

    MessageTypeEnum(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public static MessageTypeEnum fromValue(String v) {
        if (StrUtil.isBlank(v)) return null;
        for (MessageTypeEnum c : MessageTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }
}
