package com.itflower.aiplatform.ai;

import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.core.AiGeneratorServiceFacade;
import com.itflower.aiplatform.core.CodeFileSaver;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiGeneratorServiceTest {

    @Resource
    private AiGeneratorService aiGeneratorService;

    @Resource
    AiGeneratorServiceFacade aiGeneratorServiceFacade;

    @Test
    void generateHtmlPage() {
        HtmlResponse res = aiGeneratorService.generateHtmlPage("帮我生成一个个人博客页面，不超过20行代码");
        CodeFileSaver.saveHtmlFile(res);
    }

}