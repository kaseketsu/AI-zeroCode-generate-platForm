package com.itflower.aiplatform.core.saver;

import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.ai.model.MultiFileResponse;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.model.enums.GenTypeEnums;

import java.io.File;

public class CodeSaverExecutor {

    private static final HtmlCodeSaverTemplate htmlCodeSaverTemplate = new HtmlCodeSaverTemplate();

    private static final MultiFileCodeSaverTemplate multiFileCodeSaverTemplate = new MultiFileCodeSaverTemplate();

    /**
     * 保存执行器
     *
     * @param result  代码内容
     * @param genEnum 代码类型美剧
     * @return 保存位置
     */
    public static File executeSave(Object result, GenTypeEnums genEnum, Long appId) {
        return switch (genEnum) {
            case HTML -> htmlCodeSaverTemplate.saveCode((HtmlResponse) result, appId);
            case HTML_MULTI -> multiFileCodeSaverTemplate.saveCode((MultiFileResponse) result, appId);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的保存类型");
        };
    }
}
