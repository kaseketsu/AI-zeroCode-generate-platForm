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
    void generateAndSaveFileStream() {
        Flux<String> stringFlux = aiGeneratorServiceFacade.generateAndSaveFileStream("帮我生成一个个人博客页面，不超过50行代码", GenTypeEnums.VUE_MULTI, 0L);
        List<String> block = stringFlux.collectList().block();
        Assertions.assertNotNull(block);
        Assertions.assertNotNull(stringFlux);
        System.out.println(String.join("", block));
    }
}