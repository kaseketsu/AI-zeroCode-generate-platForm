package com.itflower.aiplatform.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;

@Component
@Slf4j
public class CosManager {

    @Value("${cos.bucket}")
    private String bucket;

    @Resource
    private COSClient cosClient;

    /**
     * 上传本地文件到 cos
     */
    public String uploadFile(File localFile, String path) {
        // 指定文件将要存放的存储桶
        String bucketName = bucket;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, localFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        URL objectUrl = cosClient.getObjectUrl(bucketName, path);
        return objectUrl.toString();
    }
}
