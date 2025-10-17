package com.itflower.aiplatform.model.dto.chatHistory;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatHistoryQueryRequest implements Serializable {

    private static final long serialVersionUID = -4473196353496151680L;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 核心：用于游标查询，反正深度分页
     */
    private LocalDateTime lastGenerateTime;
}
