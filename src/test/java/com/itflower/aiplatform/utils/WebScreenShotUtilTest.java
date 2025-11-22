package com.itflower.aiplatform.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class WebScreenShotUtilTest {

    @Test
    void doScreenShot() {
        String webUrl = "https://www.baidu.com";
        String res = WebScreenShotUtil.doScreenShot(webUrl);
        Assertions.assertNotNull(res);
    }
}