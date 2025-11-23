package com.itflower.aiplatform.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.service.CodeDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

@Slf4j
@Service
public class CodeDownloadServiceImpl implements CodeDownloadService {

    private static final Set<String> IGNORE_FILE_NAMES = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );

    private static final Set<String> IGNORE_FILE_TYPES = Set.of(
            ".log",
            ".tmp",
            ".cache"
    );

    private boolean isAllowedFile(Path projectDir, Path fullPath) {
        Path relativePath = projectDir.relativize(fullPath);
        for (Path part: relativePath) {
            String partString = part.toString();
            if (IGNORE_FILE_NAMES.contains(partString)) {
                return false;
            }
            if (IGNORE_FILE_TYPES.stream().anyMatch(partString::endsWith)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void downloadCodeAsZip(String projectDir, String fileName, HttpServletResponse response) {
        // 校验参数
        ThrowUtils.throwIf(StrUtil.isBlank(projectDir), ErrorCode.PARAMS_ERROR, "项目目录不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(fileName), ErrorCode.PARAMS_ERROR, "文件名不能为空");
        // 校验项目目录
        File projectDirectory = new File(projectDir);
        ThrowUtils.throwIf(!projectDirectory.exists(), ErrorCode.PARAMS_ERROR, "项目目录不存在");
        ThrowUtils.throwIf(!projectDirectory.isDirectory(), ErrorCode.PARAMS_ERROR, "指定路径不是目录");
        // 创建过滤器
        FileFilter fileFilter = file -> isAllowedFile(projectDirectory.toPath(), file.toPath());
        // 设置响应头
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".zip\"");
        // 开始压缩
        log.info("开始打包任务，地址是; {}, 文件名是: {}.zip", projectDir, fileName);
        try {
            ZipUtil.zip(response.getOutputStream(), StandardCharsets.UTF_8, false, fileFilter, projectDirectory);
            log.info("文件打包完成: {}.zip", fileName);
        } catch (Exception e) {
            log.error("压缩文件传输失败，原因是: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, String.format("压缩文件传输失败，原因是: %s", e.getMessage()));
        }
    }
}
