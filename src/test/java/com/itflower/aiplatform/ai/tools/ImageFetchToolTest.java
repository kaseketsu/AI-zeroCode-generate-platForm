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
}