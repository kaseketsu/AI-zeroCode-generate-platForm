package com.itflower.aiplatform.core.parser;

import cn.hutool.core.util.ObjUtil;
import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.model.enums.GenTypeEnums;

public class CodeParseExecutor {

    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();

    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    /**
     * 根据类型执行解析
     * @param codeContent 代码内容
     * @param genTypeEnums 类型
     * @return 解析结果
     */
    public static Object parseExecute(String codeContent, GenTypeEnums genTypeEnums) {
        ThrowUtils.throwIf(ObjUtil.isNull(genTypeEnums), ErrorCode.PARAMS_ERROR);
        return switch (genTypeEnums) {
            case HTML -> htmlCodeParser.parseCode(codeContent);
            case HTML_MULTI -> multiFileCodeParser.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的解析类型");
        };
    }
}
