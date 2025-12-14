package com.itflower.aiplatform.ai;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ImageCollectService {

    @SystemMessage(fromResource = "prompt/image-collection-system-prompt.txt")
    public String fetchImageList(@UserMessage String userMessage);
}
