package com.itflower.aiplatform.ai.tools;

import cn.hutool.core.io.FileUtil;
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
import java.nio.file.StandardOpenOption;

@Component
@Slf4j
public class ModifyFileTool extends BaseTool {

    @Tool("修改指定路径文件的代码")
    public String modifyFile(
            @P("文件相对路径")
            String relativePath,
            @P("被替换内容")
            String oldContent,
            @P("替换内容")
            String newContent,
            @ToolMemoryId Long appId
    ) {
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
        try {
            String ori = Files.readString(path);
            if (!ori.contains(oldContent)) {
                return "文件中未找到需替换内容 - %s".formatted(relativePath);
            }
            String replaced = ori.replace(oldContent, newContent);
            if (replaced.equals(oldContent)) {
                return "文件替换后未发生变化 - %s".formatted(relativePath);
            }
            Files.write(path, replaced.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("文件内容替换成功 - %s".formatted(path.toAbsolutePath()));
            return "文件替换成功 - %s".formatted(relativePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getToolName() {
        return "modifyFile";
    }

    @Override
    public String displayName() {
        return "修改文件";
    }

    @Override
    public String getToolExecutedContent(JSONObject arguments) {
        String path = arguments.getStr("relativePath");
        String content = arguments.getStr("oldContent");
        String newContent = arguments.getStr("newContent");
        return String.format("""
                \n\n[工具调用] %s %s
                
                替换前
                ```
                %s
                ```
                
                替换后
                ```
                %s
                ```
                """, displayName(), path, content, newContent);
    }
}
