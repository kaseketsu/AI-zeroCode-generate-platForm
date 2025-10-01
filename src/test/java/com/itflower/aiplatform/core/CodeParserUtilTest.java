package com.itflower.aiplatform.core;

import com.itflower.aiplatform.ai.model.HtmlResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeParserUtilTest {

    @Test
    void parseHtmlResponse() {
        String codeContent = """
                随便写一段描述：
                html 格式
                ```html
                <!DOCTYPE html>
                <html>
                <head>
                    <title>测试页面</title>
                </head>
                <body>
                    <h1>Hello World!</h1>
                </body>
                </html>
                ```
                随便写一段描述
                """;
        HtmlResponse htmlResponse = CodeParserUtil.parseHtmlCode(codeContent);
        System.out.println(htmlResponse.getHtmlCode());
    }

    @Test
    void parseMultiFileResponse() {
    }
}