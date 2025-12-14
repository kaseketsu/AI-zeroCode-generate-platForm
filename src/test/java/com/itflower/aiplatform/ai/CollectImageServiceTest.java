package com.itflower.aiplatform.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@Slf4j
@ExtendWith(SpringExtension.class)
public class CollectImageServiceTest {

    @Resource
    private ImageCollectService imageCollectService;

    @Test
    public void test() {
        final String prompt = "生成一个电商网站的架构图";
        String res = imageCollectService.fetchImageList(prompt);
        log.info(res);
    }
}
