package com.itflower.aiplatform.model.dto.app.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改 app 信息
 */
@Data
public class AppUpdateRequest implements Serializable {


    private static final long serialVersionUID = 6406163784785152036L;

    private Long id;

    private String appName;
}
