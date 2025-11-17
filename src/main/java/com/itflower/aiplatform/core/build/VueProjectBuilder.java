package com.itflower.aiplatform.core.build;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class VueProjectBuilder {
    /**
     * 通用命令行执行方法
     *
     * @param command       命令
     * @param workDirectory 工作目录
     * @param waitTime      超时时间
     * @return 是否执行成功
     */
    public boolean commandProcessor(String command, File workDirectory, long waitTime) {
        try {
            // 检查文件夹是否存在
            if (!workDirectory.exists() || !workDirectory.isDirectory()) {
                log.error("工作目录: {} 不存在", workDirectory.getAbsolutePath());
            }
            // 执行命令
            Process exec = RuntimeUtil.exec(
                    null,
                    workDirectory,
                    command.split("\\s+")
            );
            // 检测是否超时
            boolean isOverTime = exec.waitFor(waitTime, TimeUnit.SECONDS);
            if (isOverTime) {
                log.error("执行超时({}秒)", waitTime);
                return false;
            }
            // 查看是否成功
            int exitValue = exec.exitValue();
            if (exitValue != 0) {
                log.error("执行失败, 命令为: {}", command);
                return false;
            } else {
                log.info("执行成功, 命令为: {}", command);
                return true;
            }
        } catch (InterruptedException e) {
            String msg = String.format("超时时间设置失败, 在 %s, 原因是: %s", getClass().getSimpleName(), e.getMessage());
            log.error(msg);
            throw new RuntimeException(msg);
        }
    }

    public static boolean isBuildSuccess(String wordDir) {
        File workDir = new File(wordDir);
        if (!workDir.exists() || !workDir.isDirectory()) {
            return false;
        }
        File dist = new File(wordDir, "dist");
        if (!dist.exists() || !dist.isDirectory()) {
            return false;
        }
        return true;
    }


    public boolean buildProject(File workDirectory) {
        try {
            if (!workDirectory.exists() || !workDirectory.isDirectory()) {
                return false;
            }
            // 检查 package.json 是否存在
            File packageJson = new File(workDirectory, "package.json");
            if (!packageJson.exists()) {
                log.error("{} 中, packageJson 不存在", workDirectory);
                return false;
            }
            log.info("开始构建 vue 项目, {}", packageJson.getAbsolutePath());
            if (!executeNpmInstall(workDirectory)) {
                log.error("npm install 执行失败");
                return false;
            }
            if (!executeNpmBuild(workDirectory)) {
                log.error("npm run build 执行失败");
                return false;
            }
            File dist = new File(workDirectory, "dist");
            if (!dist.exists()) {
                log.error("构建完成，但 dist 目录未生成, {}", dist.getAbsolutePath());
                return false;
            }
            log.info("vue 项目构建完成, dist 目录：{}", dist.getAbsolutePath());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void buildProjectAsync(File workDirectory) {
        Thread.ofVirtual().name("vue_builder" + System.currentTimeMillis()).start(() -> {
            try {
                buildProject(workDirectory);
            } catch (Exception e) {
                String msg = String.format("异步构建 vue 项目失败，原因是: %s", e.getMessage());
                log.error(msg);
                throw new RuntimeException(msg);
            }
        });
    }

    /**
     * 查看是否是 windows 系统
     *
     * @return 是 / 否
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private String buildCommand(String command) {
        if (isWindows()) {
            return command + ".cmd";
        }
        return command;
    }

    private boolean executeNpmInstall(File workDirectory) {
        log.info("执行npm install...");
        String npmInstall = buildCommand("npm install");
        return commandProcessor(npmInstall, workDirectory, 300);
    }

    private boolean executeNpmBuild(File workDirectory) {
        log.info("执行 npm run build...");
        String npmBuild = buildCommand("npm run build");
        return commandProcessor(npmBuild, workDirectory, 180);
    }
}
