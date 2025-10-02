package com.itflower.aiplatform.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.itflower.aiplatform.common.exception.ThrowUtils;
import com.itflower.aiplatform.model.enums.GenTypeEnums;

import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class CodeFileSaverTemplate<T> {

    // 文件根目录 tmp/code_output/biztype_id_filename
    private static final String FILE_ROOT_PATH = System.getProperty("user.dir") +
            File.separator + "tmp" + File.separator + "code_output" + File.separator;

    /**
     * 保存文件
     *
     * @param result 代码数据
     * @return 文件
     */
    protected final File saveCode(T result) {
        // 1. 校验参数
        validateInput(result);

        // 2. 生成唯一路径
        String path = buildUniquePath();

        // 3. 保存文件
        saveFiles(path, result);

        // 4. 返回文件
        return new File(path);
    }

    /**
     * 保存文件
     *
     * @param path     路径
     * @param fileName 文件名
     * @param content  内容
     */
    protected static void saveFile(String path, String fileName, String content) {
        String filePath = path + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 创建唯一路径
     *
     * @return 唯一路径
     */
    protected String buildUniquePath() {
        String bz_type = getCodeType().getValue();
        String baseDirPath = StrUtil.format("{}_{}", bz_type, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_ROOT_PATH + File.separator + baseDirPath;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 校验输入
     *
     * @param result 代码数据
     */
    protected void validateInput(T result) {
        ThrowUtils.throwIf(ObjUtil.isNull(result), ErrorCode.PARAMS_ERROR);
    }

    /**
     * 获取代码类型
     *
     * @return 代码类型
     */
    protected abstract GenTypeEnums getCodeType();

    /**
     * 保存文件
     *
     * @param path   路径
     * @param result 代码数据
     */
    protected abstract void saveFiles(String path, T result);
}
