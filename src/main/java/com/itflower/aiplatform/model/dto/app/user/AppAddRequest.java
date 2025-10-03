package com.itflower.aiplatform.model.dto.app.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppAddRequest implements Serializable {

    private static final long serialVersionUID = -6505967141367588463L;
    /**
     * 初始提示词
     */
    private String initialPrompt;

}
