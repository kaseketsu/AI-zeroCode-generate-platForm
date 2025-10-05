package com.itflower.aiplatform.model.dto.app.user;

import lombok.Data;

import java.io.Serializable;

/**
 * app 部署请求
 */
@Data
public class AppDeployRequest implements Serializable {

    private static final long serialVersionUID = 3123729671546560208L;

    /**
     * 应用 Id
     */
    Long appId;
}
