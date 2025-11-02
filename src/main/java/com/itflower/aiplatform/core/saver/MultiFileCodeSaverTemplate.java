package com.itflower.aiplatform.core.saver;

import cn.hutool.core.util.StrUtil;
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
        // 有些对话可能和生成页面无关
        if (StrUtil.isNotBlank(result.getHtmlCode())) {
            saveFile(path, "index.html", result.getHtmlCode());
        }
        if (StrUtil.isNotBlank(result.getCssCode())) {
            saveFile(path, "style.css", result.getCssCode());
        }
        if (StrUtil.isNotBlank(result.getJsCode())) {
            saveFile(path, "script.js", result.getJsCode());
        }
    }
}
