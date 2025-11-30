package com.itflower.aiplatform.ai.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.itflower.aiplatform.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class ReadFileTool extends BaseTool {

    @Tool
    public String readFile(
            @P("文件相对路径")
            String relativePath,
            @ToolMemoryId Long appId
    ) {
        try {
            if (StrUtil.isBlank(relativePath)) {
                return "指定路径为空";
            }
            Path path = Paths.get(relativePath);
            if (!path.isAbsolute()) {
                String dir = "vue_project_%s".formatted(appId);
                Path absPath = Paths.get(AppConstant.OUT_PUT_PATH, dir);
                path = absPath.resolve(path);
            }
            // 判断文件是否存在
            if (!Files.exists(path)) {
                return "指定位置文件不存在";
            }
            // 判断文件是否是常规文件
            if (!Files.isRegularFile(path)) {
                return "指定位置非常规文件";
            }
            String res = Files.readString(path);
            log.info("文件读取成功 -" + relativePath);
            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getToolName() {
        return "readFile";
    }

    @Override
    public String displayName() {
        return "读取文件";
    }

    @Override
    public String getToolExecutedContent(JSONObject arguments) {
        String path = arguments.getStr("relativePath");
        return String.format("\n\n[工具调用] %s %s \n\n", displayName(), path);
    }
}
