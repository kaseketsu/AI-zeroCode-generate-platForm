package com.itflower.aiplatform.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum ImageTypeEnum {

    CONTENT("内容图片", "content"),
    LOGO("logo图片", "logo"),
    ILLUSTRATION("插画图片", "illustration"),
    ARCHITECTURE("架构图片", "architecture");

    private final String description;

    private final String value;

    ImageTypeEnum(String description, String value) {
        this.description = description;
        this.value = value;

    }

    public static ImageTypeEnum getEnum(String value) {
        if (StringUtils.isBlank(value)) return null;
        for (ImageTypeEnum e : ImageTypeEnum.values()) {
            if (e.value.equals(value)) return e;
        }
        return null;
    }
}
