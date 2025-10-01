package com.itflower.aiplatform.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("多文件代码响应")
public class MultiFileResponse {

    @Description("HTML代码")
    private String htmlCode;

    @Description("描述")
    private String description;

    @Description("CSS代码")
    private String cssCode;

    @Description("JS代码")
    private String jsCode;
}
