package com.itflower.aiplatform.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.itflower.aiplatform.ai.model.HtmlResponse;
import com.itflower.aiplatform.ai.model.MultiFileResponse;
import com.itflower.aiplatform.model.enums.GenTypeEnums;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class CodeFileSaver {

    // 文件根目录 tmp/code_output/biztype_id_filename
    private static final String FILE_ROOT_PATH = System.getProperty("user.dir") +
            File.separator + "tmp" + File.separator + "code_output" + File.separator;

    /**
     * 构建唯一路径
     *
     * @param bizType 业务类型
     * @return 唯一路径
     */
    private static String buildUniquePath(String bizType) {
        String dirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_ROOT_PATH + dirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 保存文件
     *
     * @param path     路径
     * @param fileName 文件名
     * @param content  内容
     */
    private static void saveFile(String path, String fileName, String content) {
        String filePath = path + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 保存 HTML 文件
     *
     * @param htmlResponse HTML 响应
     * @return 文件
     */
    public static File saveHtmlFile(HtmlResponse htmlResponse) {
        String path = buildUniquePath(GenTypeEnums.HTML.getValue());
        saveFile(path, "index.html", htmlResponse.getHtmlCode());
        return new File(path);
    }

    /**
     * 保存 HTML 多文件
     *
     * @param multiFileResponse HTML 多文件响应
     * @return 文件
     */
    public static File saveHtmlMultiFile(MultiFileResponse multiFileResponse) {
        String path = buildUniquePath(GenTypeEnums.HTML_MULTI.getValue());
        saveFile(path, "index.html", multiFileResponse.getHtmlCode());
        saveFile(path, "style.css", multiFileResponse.getCssCode());
        saveFile(path, "script.js", multiFileResponse.getJsCode());
        return new File(path);
    }
}
