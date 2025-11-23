package com.itflower.aiplatform.service;


import jakarta.servlet.http.HttpServletResponse;

public interface CodeDownloadService {

    /**
     * 通过 response 的输出流传递 zip 文件
     *
     * @param projectDir 项目代码目录
     * @param fileName   zip 文件名
     * @param response   servletResp
     */
    void downloadCodeAsZip(String projectDir, String fileName, HttpServletResponse response);
}
