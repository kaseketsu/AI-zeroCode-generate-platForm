package com.itflower.aiplatform.core;

import cn.hutool.core.util.ObjUtil;
import com.itflower.aiplatform.ai.AiGeneratorService;
import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.ai.model.MultiFileResponse;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AiGeneratorServiceFacade {

    @Resource
    private AiGeneratorService aiGeneratorService;

    /**
     * 生成并保存 HTML 页面
     *
     * @param userMessage 用户输入的消息
     * @return 生成的 HTML 页面文件
     */
    private File generateAndSaveHtmlPage(String userMessage) {
        HtmlResponse htmlResponse = aiGeneratorService.generateHtmlPage(userMessage);
        File file = CodeFileSaver.saveHtmlFile(htmlResponse);
        return file;
    }

    /**
     * 生成并保存 HTML 多文件页面
     *
     * @param userMessage 用户输入的消息
     * @return 生成的 HTML 多文件页面文件
     */
    private File generateAndSaveHtmlMultiFilePage(String userMessage) {
        MultiFileResponse multiFileResponse = aiGeneratorService.generateMultiFileHtmlPage(userMessage);
        File file = CodeFileSaver.saveHtmlMultiFile(multiFileResponse);
        return file;
    }

    /**
     * 唯一接口，生成和保存文件
     *
     * @param userMessage  用户输入的消息
     * @param genTypeEnums 生成类型
     * @return 生成的文件
     */
    public File generateAndSaveFile(String userMessage, GenTypeEnums genTypeEnums) {
        ThrowUtils.throwIf(ObjUtil.isNull(genTypeEnums), ErrorCode.PARAMS_ERROR);
        return switch (genTypeEnums.getValue()) {
            case "html" -> generateAndSaveHtmlPage(userMessage);
            case "html_multi" -> generateAndSaveHtmlMultiFilePage(userMessage);
            default -> throw new RuntimeException("不支持的生成类型, " + genTypeEnums.getValue());
        };
    }
}
