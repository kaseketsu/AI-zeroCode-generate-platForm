package com.itflower.aiplatform.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileWriteTool {

    @Tool("写入文件到具体的路径")
    public String writeFile(
            @P("文件的相对路径")
            String relativePath,
            @P("具体写入内容")
            String content
    ) {
        return null;
    }
}
