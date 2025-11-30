package com.itflower.aiplatform.ai.tools;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ToolManager {

    private final Map<String, BaseTool> toolMap = new HashMap<>();

    @Resource
    private BaseTool[] baseTools;

    @PostConstruct
    public void init() {
        for (BaseTool baseTool : baseTools) {
            toolMap.put(baseTool.getToolName(), baseTool);
            log.info("注册工具: {} -> {}", baseTool.getToolName(), baseTool.displayName());
        }
        log.info("工具注册完成，共 {} 个工具", toolMap.size());
    }

    public BaseTool getTool(String toolName) {
        return toolMap.get(toolName);
    }

    public BaseTool[] getTools() {
        return baseTools;
    }
}
