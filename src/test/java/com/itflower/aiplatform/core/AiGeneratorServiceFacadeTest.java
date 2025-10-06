package com.itflower.aiplatform.core;

import com.itflower.aiplatform.model.enums.GenTypeEnums;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiGeneratorServiceFacadeTest {

    @Resource
    private AiGeneratorServiceFacade aiGeneratorServiceFacade;

    @Test
    void generateAndSaveFile() {
        File file = aiGeneratorServiceFacade.generateAndSaveFile("帮我生成一个个人博客页面，不超过20行代码", GenTypeEnums.HTML);
        Assertions.assertNotNull(file);
    }

    @Test
    void testGenerateAndSaveFile() {
        File file = aiGeneratorServiceFacade.generateAndSaveFile("帮我生成一个个人博客页面，不超过20行代码", GenTypeEnums.HTML_MULTI);
        Assertions.assertNotNull(file);
    }

    @Test
    void generateAndSaveFileStream() {
        Flux<String> stringFlux = aiGeneratorServiceFacade.generateAndSaveFileStream("帮我生成一个个人博客页面，不超过50行代码", GenTypeEnums.HTML_MULTI);
        List<String> block = stringFlux.collectList().block();
        Assertions.assertNotNull(block);
        Assertions.assertNotNull(stringFlux);
    }
}