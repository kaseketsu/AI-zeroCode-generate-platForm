package com.itflower.aiplatform.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum GenTypeEnums {

    HTML("HTML 原生模板代码", "html"),
    HTML_MULTI("HTML 多文件模板代码", "html_multi"),
    VUE_MULTI("vue 多文件模板代码", "vue_multi");

    // 描述
    private final String desc;

    // 值
    private final String value;

    GenTypeEnums(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }

    /**
     * 根据值获取枚举
     * @param value 值
     * @return 枚举
     */
    public static GenTypeEnums getEnumByValue(String value) {
        if (ObjUtil.isNull(value)){
            return null;
        }
        for (GenTypeEnums genTypeEnums : GenTypeEnums.values()) {
            if (genTypeEnums.getValue().equals(value)) {
                return genTypeEnums;
            }
        }
        return null;
    }
}
