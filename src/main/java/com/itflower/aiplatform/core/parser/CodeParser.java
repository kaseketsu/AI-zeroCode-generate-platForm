package com.itflower.aiplatform.core.parser;

/**
 * 代码解析器
 *
 * @param <T> 解析后的数据类型
 */
public interface CodeParser<T> {

    /**
     * 解析代码
     *
     * @param codeContent 代码内容
     * @return 解析后的数据
     */
    T parseCode(String codeContent);
}
