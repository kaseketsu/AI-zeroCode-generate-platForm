package com.itflower.aiplatform.core.saver;

import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.model.enums.GenTypeEnums;

/**
 * HTML代码保存模板
 */
public class HtmlCodeSaverTemplate extends CodeFileSaverTemplate<HtmlResponse> {

    /**
     * 获取代码类型
     *
     * @return 代码类型
     */
    @Override
    protected GenTypeEnums getCodeType() {
        return GenTypeEnums.HTML;
    }

    /**
     * 保存文件
     *
     * @param path   路径
     * @param result 代码数据
     */
    @Override
    protected void saveFiles(String path, HtmlResponse result) {
        saveFile(path, "index.html", result.getHtmlCode());
    }
}
