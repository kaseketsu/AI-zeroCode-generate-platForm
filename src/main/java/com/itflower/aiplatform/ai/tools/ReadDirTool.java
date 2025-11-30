package com.itflower.aiplatform.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.itflower.aiplatform.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Component
public class ReadDirTool extends BaseTool {

    /**
     * 需要忽略的文件和目录
     */
    private static final Set<String> IGNORED_NAMES = Set.of(
            "node_modules", ".git", "dist", "build", ".DS_Store",
            ".env", "target", ".mvn", ".idea", ".vscode", "coverage"
    );

    /**
     * 需要忽略的文件扩展名
     */
    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log", ".tmp", ".cache", ".lock"
    );

    @Tool("读取指定路径下的文件")
    public String readDir(
            @P("目录路径")
            String relativeDirPath,
            @ToolMemoryId Long appId
    ) {
        Path path = Paths.get(relativeDirPath == null ? "" : relativeDirPath);
        if (!path.isAbsolute()) {
            String dirPath = String.format("vue_project_%s", appId);
            Path absPath = Paths.get(AppConstant.OUT_PUT_PATH, dirPath);
            path = absPath.resolve(relativeDirPath == null ? "" : relativeDirPath);
        }
        if (!Files.isDirectory(path) || !Files.exists(path)) {
            return "错误: 文件不存在或不是目录 - %s".formatted(relativeDirPath);
        }
        List<File> files = FileUtil.loopFiles(path, f -> !shouldIgnore(f));
        StrBuilder strBuilder = new StrBuilder("项目结构如下: \n");
        File root = path.toFile();
        files.stream()
                .sorted((f1, f2) -> {
                    int dp1 = getDepth(root, f1);
                    int dp2 = getDepth(root, f2);
                    if (dp1 != dp2) {
                        return Integer.compare(dp1, dp2);
                    }
                    return f1.getName().compareTo(f2.getName());
                }).forEach(f -> {
                    int indent = getDepth(root, f);
                    strBuilder.append("    ".repeat(indent)).append(f.getName()).append("\n");
                });
        return strBuilder.toString();
    }

    private boolean shouldIgnore(File file) {
        if (file == null) {
            return true;
        }
        return IGNORED_NAMES.contains(file.getName()) || IGNORED_EXTENSIONS.stream().anyMatch(file.getName()::endsWith);
    }

    private int getDepth(File root, File cur) {
        Path rootPath = root.toPath();
        Path curPath = cur.toPath();
        return rootPath.relativize(curPath).getNameCount() - 1;
    }

    @Override
    public String getToolName() {
        return "readDir";
    }

    @Override
    public String displayName() {
        return "读取目录";
    }

    @Override
    public String getToolExecutedContent(JSONObject arguments) {
        String relativeFileDirPath = arguments.getStr("relativeDirPath");
        if (StrUtil.isBlank(relativeFileDirPath)) {
            relativeFileDirPath = "根目录";
        }
        return String.format("\n\n[工具调用] %s %s\n\n", displayName(), relativeFileDirPath);
    }
}
