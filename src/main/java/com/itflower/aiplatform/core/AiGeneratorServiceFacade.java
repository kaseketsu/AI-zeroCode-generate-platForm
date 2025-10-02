package com.itflower.aiplatform.core;

import cn.hutool.core.util.ObjUtil;
import com.itflower.aiplatform.ai.AiGeneratorService;
import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.ai.model.MultiFileResponse;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.core.parser.CodeParseExecutor;
import com.itflower.aiplatform.core.saver.CodeSaverExecutor;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.swing.text.html.HTML;
import java.io.File;


@Service
@Slf4j
public class AiGeneratorServiceFacade {

    @Resource
    private AiGeneratorService aiGeneratorService;

    /**
     * 统一处理流式输出
     *
     * @param stream  输出流
     * @param genEnum 代码类型
     * @return 输出流
     */
    private Flux<String> processStreamCode(Flux<String> stream, GenTypeEnums genEnum) {
        StringBuilder sb = new StringBuilder();
        return stream
                .doOnNext(chunk -> {
                    sb.append(chunk);
                })
                .doOnComplete(() -> {
                    try {
                        String res = sb.toString();
                        Object parseRes = CodeParseExecutor.parseExecute(res, genEnum);
                        File file = CodeSaverExecutor.executeSave(parseRes, genEnum);
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
    public File generateAndSaveFile(String userMessage, GenTypeEnums genTypeEnums) {
        ThrowUtils.throwIf(ObjUtil.isNull(genTypeEnums), ErrorCode.PARAMS_ERROR);
        return switch (genTypeEnums) {
            case HTML -> {
                HtmlResponse htmlResponse = aiGeneratorService.generateHtmlPage(userMessage);
                yield CodeSaverExecutor.executeSave(htmlResponse, genTypeEnums);
            }
            case HTML_MULTI -> {
                MultiFileResponse multiFileResponse = aiGeneratorService.generateMultiFileHtmlPage(userMessage);
                yield CodeSaverExecutor.executeSave(multiFileResponse, genTypeEnums);
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
    public Flux<String> generateAndSaveFileStream(String userMessage, GenTypeEnums genTypeEnums) {
        ThrowUtils.throwIf(ObjUtil.isNull(genTypeEnums), ErrorCode.PARAMS_ERROR);
        return switch (genTypeEnums) {
            case HTML -> {
                Flux<String> stringFlux = aiGeneratorService.generateHtmlPageStream(userMessage);
                yield processStreamCode(stringFlux, GenTypeEnums.HTML);
            }
            case HTML_MULTI -> {
                Flux<String> stringFlux = aiGeneratorService.generateMultiFileHtmlPageStream(userMessage);
                yield processStreamCode(stringFlux, GenTypeEnums.HTML_MULTI);
            }
            default -> throw new RuntimeException("不支持的生成类型, " + genTypeEnums.getValue());
        };
    }

}
