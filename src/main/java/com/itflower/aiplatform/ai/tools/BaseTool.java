package com.itflower.aiplatform.ai.tools;

import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseTool {

    /**
     * 获取工具名称
     *
     * @return 工具名称
     */
    public abstract String getToolName();

    /**
     * 获取工具中文名
     *
     * @return 中文名称
     */
    public abstract String displayName();

    /**
     * 获取请求内容
     *
     * @return 请求内容
     */
    public String getRequestContent() {
        return String.format("\n\n[选择工具] %s\n\n", displayName());
    }

    /**
     * 获取工具执行内容
     *
     * @param arguments 工具调用参数
     * @return 执行内容
     */
    public abstract String getToolExecutedContent(JSONObject arguments);
}
