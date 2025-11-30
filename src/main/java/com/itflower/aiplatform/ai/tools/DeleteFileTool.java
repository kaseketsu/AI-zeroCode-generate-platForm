package com.itflower.aiplatform.ai.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
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
import java.util.Set;

@Component
@Slf4j
public class DeleteFileTool extends BaseTool {





    @Tool("删除指定位置的文件")
    public String deleteFile(
            @P("文件相对路径")
            String relativePath,
            @ToolMemoryId Long appId
    ) {
        if (StrUtil.isBlank(relativePath)) {
            return "指定路径为空";
        }
        Path path = Paths.get(relativePath);
        if (!path.isAbsolute()) {
            String dirPath = String.format("vue_project_%s", appId);
            Path absPath = Paths.get(AppConstant.OUT_PUT_PATH, dirPath);
            path = absPath.resolve(relativePath);
        }
        // 判断文件是否存在
        if (!Files.exists(path)) {
            return "指定位置文件不存在";
        }
        // 判断文件是否是常规文件
        if (!Files.isRegularFile(path)) {
            return "指定位置非常规文件";
        }
        // 判断是否是重要文件
        if (isImportantFile(path.getFileName().toString())) {
            return "警告: 重要文件，不能删除";
        }
        try {
            Files.delete(path);
            log.info("文件删除成功");
            return "文件删除成功 : %s".formatted(relativePath);
        } catch (IOException e) {
            String msg = String.format("删除文件发生错误，原因是: %s", e.getMessage());
            log.error(msg, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, msg);
        }
    }


    /**
     * 判断是否是重要文件，不允许删除
     */
    private boolean isImportantFile(String fileName) {
        String[] importantFiles = {
                "package.json", "package-lock.json", "yarn.lock", "pnpm-lock.yaml",
                "vite.config.js", "vite.config.ts", "vue.config.js",
                "tsconfig.json", "tsconfig.app.json", "tsconfig.node.json",
                "index.html", "main.js", "main.ts", "App.vue", ".gitignore", "README.md"
        };
        for (String important : importantFiles) {
            if (important.equalsIgnoreCase(fileName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getToolName() {
        return "deleteFile";
    }

    @Override
    public String displayName() {
        return "删除文件";
    }

    @Override
    public String getToolExecutedContent(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativePath");
        return String.format("\n\n[工具调用] %s %s\n\n", displayName(), relativeFilePath);
    }
}
