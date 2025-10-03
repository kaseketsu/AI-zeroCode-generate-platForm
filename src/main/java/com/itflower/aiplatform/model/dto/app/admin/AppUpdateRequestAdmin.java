package com.itflower.aiplatform.model.dto.app.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改 app 信息
 */
@Data
public class AppUpdateRequestAdmin implements Serializable {

    private static final long serialVersionUID = -2990191532942882682L;

    private Long id;

    private String appName;

    private Integer priority;

    private String cover;
}
