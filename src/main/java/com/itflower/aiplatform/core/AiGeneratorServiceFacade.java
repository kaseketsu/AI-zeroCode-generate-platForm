package com.itflower.aiplatform.core;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.itflower.aiplatform.ai.AiGeneratorService;
import com.itflower.aiplatform.ai.AiGeneratorServiceFactory;
import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.ai.model.MultiFileResponse;
import com.itflower.aiplatform.ai.model.message.AiStreamingMessage;
import com.itflower.aiplatform.ai.model.message.ToolExecutedMessage;
import com.itflower.aiplatform.ai.model.message.ToolRequestMessage;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.core.parser.CodeParseExecutor;
import com.itflower.aiplatform.core.saver.CodeSaverExecutor;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;


@Service
@Slf4j
public class AiGeneratorServiceFacade {

    @Resource
    private AiGeneratorServiceFactory serviceFactory;

    /**
     * 统一处理流式输出
     *
     * @param stream  输出流
     * @param genEnum 代码类型
     * @return 输出流
     */
    private Flux<String> processStreamCode(Flux<String> stream, GenTypeEnums genEnum, Long appId) {
        StringBuilder sb = new StringBuilder();
        return stream
                .doOnNext(chunk -> {
                    sb.append(chunk);
                })
                .doOnComplete(() -> {
                    try {
                        String res = sb.toString();
                        Object parseRes = CodeParseExecutor.parseExecute(res, genEnum);
                        File file = CodeSaverExecutor.executeSave(parseRes, genEnum, appId);
                        log.info("生成并保存 {} 页面 (流式) 成功: {}", genEnum.getValue(), file.getAbsolutePath());
                    } catch (Exception e) {
                        log.info("生成并保存 {} 页面 (流式) 失败: {}", genEnum.getValue(), e.getMessage());
                    }
                });
    }


    /**
     * 生成和保存文件
     *
     * @param userMessage  用户输入的消息
     * @param genTypeEnums 生成类型
     * @return 生成的文件
     */
    public File generateAndSaveFile(String userMessage, GenTypeEnums genTypeEnums, Long appId) {
        ThrowUtils.throwIf(ObjUtil.isNull(genTypeEnums), ErrorCode.PARAMS_ERROR);
        // 为每个应用创建一个唯一的 aiService
        AiGeneratorService aiGeneratorService = serviceFactory.getAiGeneratorService(appId);
        return switch (genTypeEnums) {
            case HTML -> {
                HtmlResponse htmlResponse = aiGeneratorService.generateHtmlPage(userMessage);
                yield CodeSaverExecutor.executeSave(htmlResponse, genTypeEnums, appId);
            }
            case HTML_MULTI -> {
                MultiFileResponse multiFileResponse = aiGeneratorService.generateMultiFileHtmlPage(userMessage);
                yield CodeSaverExecutor.executeSave(multiFileResponse, genTypeEnums, appId);
            }
            default -> throw new RuntimeException("不支持的生成类型, " + genTypeEnums.getValue());
        };
    }

    /**
     * 唯一接口，生成和保存文件 (流式)
     *
     * @param userMessage  用户输入的消息
     * @param genTypeEnums 生成类型
     * @return 生成的文件
     */
    public Flux<String> generateAndSaveFileStream(String userMessage, GenTypeEnums genTypeEnums, Long appId) {
        ThrowUtils.throwIf(ObjUtil.isNull(genTypeEnums), ErrorCode.PARAMS_ERROR);
        AiGeneratorService aiGeneratorService = serviceFactory.getAiGeneratorService(appId, genTypeEnums);
        return switch (genTypeEnums) {
            case HTML -> {
                Flux<String> stringFlux = aiGeneratorService.generateHtmlPageStream(userMessage);
                yield processStreamCode(stringFlux, GenTypeEnums.HTML, appId);
            }
            case HTML_MULTI -> {
                Flux<String> stringFlux = aiGeneratorService.generateMultiFileHtmlPageStream(userMessage);
                yield processStreamCode(stringFlux, GenTypeEnums.HTML_MULTI, appId);
            }
            case VUE_MULTI -> {
                Flux<String> stringFlux = aiGeneratorService.generateMultiFileVuePageStream(appId, userMessage);
                yield processStreamCode(stringFlux, GenTypeEnums.VUE_MULTI, appId);
            }
            default -> throw new RuntimeException("不支持的生成类型, " + genTypeEnums.getValue());
        };
    }

    /**
     * 江 tokenStream 适配为 flux
     *
     * @param tokenStream 包含工具调用相关信息
     * @return 适配后的工作流
     */
    public Flux<String> processVueStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse(rep -> {
                String jsonStr = JSONUtil.toJsonStr(new AiStreamingMessage(rep));
                sink.next(jsonStr);
            }).onPartialToolExecutionRequest((idx, toolReq) -> {
                String jsonStr = JSONUtil.toJsonStr(new ToolRequestMessage(toolReq));
                sink.next(jsonStr);
            }).onToolExecuted(toolExec -> {
                String jsonStr = JSONUtil.toJsonStr(new ToolExecutedMessage(toolExec));
                sink.next(jsonStr);
            }).onCompleteResponse(rep -> {
                sink.complete();
            }).onError(throwable -> {
                log.error("tokenStream 转换失败，原因为: {}", throwable.getMessage());
                sink.error(throwable);
            }).start();
        });
    }

}
