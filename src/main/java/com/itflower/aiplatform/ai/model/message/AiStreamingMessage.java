package com.itflower.aiplatform.ai.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AiStreamingMessage extends StreamingMessage {

    private String data;

    public AiStreamingMessage(String data) {
        super(StreamingMessageTypeEnum.AI_RESPONSE.getValue());
        this.data = data;
    }
}
