package com.itflower.aiplatform.ai;

import com.itflower.aiplatform.model.enums.GenTypeEnums;
import dev.langchain4j.service.SystemMessage;

public interface AiRoutingService {

    @SystemMessage(fromResource = "prompt/codegen-ai-routing-system-prompt.txt")
    GenTypeEnums aiRouting(String userMessage);
}
