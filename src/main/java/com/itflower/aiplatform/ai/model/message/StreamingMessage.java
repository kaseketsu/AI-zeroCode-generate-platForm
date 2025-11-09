package com.itflower.aiplatform.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 所有流式信息的基类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamingMessage {

    /**
     * 消息类型
     */
    private String type;
}
