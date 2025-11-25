package com.itflower.aiplatform.utils;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itflower.aiplatform.AiPlatformApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MarkdownParserTest {

    @Test
    void parseMarkdownTables() {
        try {
            String s = FileUtils.readFileToString(new File("src/test/java/com/itflower/aiplatform/test.txt"));
            List<Map<String, String>> maps = MarkdownParser.parseMarkdownTables(s);
            Assert.notEmpty(maps, "空");
            ObjectMapper objectMapper = new ObjectMapper();
            String s1 = objectMapper.writeValueAsString(maps);
            log.info(s1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}