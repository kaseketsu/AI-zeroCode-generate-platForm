package com.itflower.aiplatform.ai;

import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.ai.model.MultiFileResponse;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * AI 生成器服务接口
 */
public interface AiGeneratorService {

    /**
     * 根据 prompt 生成单 HTML 页面
     *
     * @param userMessage prompt
     * @return HTML 页面
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlResponse generateHtmlPage(String userMessage);

    /**
     * 根据 prompt 生成多 HTML 页面
     *
     * @param userMessage prompt
     * @return HTML 页面
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiFileResponse generateMultiFileHtmlPage(String userMessage);

    /**
     * 根据 prompt 生成单 HTML 页面（流式）
     *
     * @param userMessage prompt
     * @return HTML 页面
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlPageStream(String userMessage);

    /**
     * 根据 prompt 生成多 HTML 页面（流式）
     *
     * @param userMessage prompt
     * @return HTML 页面
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileHtmlPageStream(String userMessage);

    /**
     * 生成 vue 项目页面（流式）
     *
     * @param userMessage prompt
     * @return vue 页面
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-project-system-prompt.txt")
    TokenStream generateMultiFileVuePageStream(@MemoryId Long memoryId, @UserMessage String userMessage);
}
