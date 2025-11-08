package com.itflower.aiplatform.ai.tools;

import cn.hutool.core.util.StrUtil;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.MemoryId;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class FileWriteTool {

    @Tool("写入文件到具体的路径")
    public String writeFile(
            @P("文件的相对路径")
            String relativePath,
            @P("具体写入内容")
            String content,
            @MemoryId Long appId
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
}
