package com.itflower.aiplatform.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
@Description("HTML响应")
public class HtmlResponse {

    @Description("HTML代码")
    private String htmlCode;

    @Description("描述")
    private String description;
}
