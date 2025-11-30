package com.itflower.aiplatform.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.service.MemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Component
public class FileWriteTool extends BaseTool {

    @Tool("写入文件到具体的路径")
    public String writeFile(
            @P("文件的相对路径")
            String relativePath,
            @P("具体写入内容")
            String content,
            @ToolMemoryId Long appId
    ) {
        try {
            ThrowUtils.throwIf(StrUtil.isBlank(relativePath), ErrorCode.PARAMS_ERROR, "vue 文件写入路径为空");
            Path path = Paths.get(relativePath);
            // 转为绝对路径
            if (!path.isAbsolute()) {
                String directory = "vue_project_" + appId;
                Path directoryPath = Paths.get(AppConstant.OUT_PUT_PATH, directory);
                path = directoryPath.resolve(relativePath);
            }
            // 创建父目录
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // 不存在就创建，存在就覆盖
            Files.write(
                    path,
                    content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            log.info("文件写入完成，路径为: {}", path.toAbsolutePath());
            return "vue 文件写入路径为: " + relativePath;
        } catch (Exception e) {
            log.error("写入文件失败, 原因: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getToolName() {
        return "writeFile";
    }

    @Override
    public String displayName() {
        return "写入文件";
    }

    @Override
    public String getToolExecutedContent(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativePath");
        String content = arguments.getStr("content");
        String suffix = FileUtil.getSuffix(relativeFilePath);
        return String.format("""
                [工具调用] %s %s
                ``` %s
                %s
                ```
                """, displayName(), relativeFilePath, suffix, content);
    }
}
