package com.itflower.aiplatform.model.entity;

import com.itflower.aiplatform.model.enums.ImageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResource {

    /**
     * 图片描述
     */
    private String description;

    /**
     * 图片类型
     */
    private ImageTypeEnum imageType;

    /**
     * 图片地址
     */
    private String imageUrl;
}
