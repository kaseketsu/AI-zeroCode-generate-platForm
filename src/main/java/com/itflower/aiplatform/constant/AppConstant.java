package com.itflower.aiplatform.constant;

import java.io.File;

public interface AppConstant {

    /**
     * 精选应用的优先级
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * 默认应用优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 文件输出路径
     */
    String OUT_PUT_PATH = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 文件部署路径
     */
    String DEPLOY_PATH = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * 资源访问基路径
     */
    String HOST_PATH = "http://localhost";
}
