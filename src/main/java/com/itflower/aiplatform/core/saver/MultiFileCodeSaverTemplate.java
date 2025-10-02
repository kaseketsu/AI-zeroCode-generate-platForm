package com.itflower.aiplatform.core.saver;

import com.itflower.aiplatform.ai.model.MultiFileResponse;
import com.itflower.aiplatform.model.enums.GenTypeEnums;

/**
 * 多文件保存模板
 */
public class MultiFileCodeSaverTemplate extends CodeFileSaverTemplate<MultiFileResponse> {

    /**
     * 获取代码类型
     *
     * @return 代码类型
     */
    @Override
    protected GenTypeEnums getCodeType() {
        return GenTypeEnums.HTML_MULTI;
    }

    /**
     * 保存文件
     *
     * @param path   路径
     * @param result 代码数据
     */
    @Override
    protected void saveFiles(String path, MultiFileResponse result) {
        saveFile(path, "index.html", result.getHtmlCode());
        saveFile(path, "style.css", result.getCssCode());
        saveFile(path, "script.js", result.getJsCode());
    }
}
