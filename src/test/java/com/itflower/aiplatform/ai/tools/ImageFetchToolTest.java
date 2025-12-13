package com.itflower.aiplatform.ai.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itflower.aiplatform.model.entity.ImageResource;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class ImageFetchToolTest {

    @Resource
    private ImageFetchTool contentImageFetchTool;

    @Test
    void fetchImageList() throws JsonProcessingException {
        List<ImageResource> scenes  = contentImageFetchTool.fetchContentImageList("scene");
        ObjectMapper objectMapper = new ObjectMapper();
        String res = objectMapper.writeValueAsString(scenes);
        log.info(res);
    }

    @Test
    void fetchIllustration() throws JsonProcessingException {
        List<ImageResource> happy = contentImageFetchTool.fetchIllustrationList("happy");
        ObjectMapper objectMapper = new ObjectMapper();
        String res = objectMapper.writeValueAsString(happy);
        log.info(res);
    }

    @Test
    void fetchMermaid() throws JsonProcessingException {
        final String mermaid = """
                graph TD
                    A[开始] --> B{条件判断}
                    B -->|是| C[执行操作]
                    B -->|否| D[结束]
                    C --> E[完成处理]
                    E --> F((结束))
                   \s
                    style A fill:#90EE90
                    style F fill:#FFB6C1
                """;
        List<ImageResource> happy = contentImageFetchTool.fetchMermaidPicList(mermaid, "流程图");
        ObjectMapper objectMapper = new ObjectMapper();
        String res = objectMapper.writeValueAsString(happy);
        log.info(res);
    }
}