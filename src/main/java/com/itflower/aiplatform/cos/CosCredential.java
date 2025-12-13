package com.itflower.aiplatform.cos;

import com.itflower.aiplatform.common.exception.BusinessException;
import com.itflower.aiplatform.common.exception.ErrorCode;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.TreeMap;

@Component
@Slf4j
public class CosCredential {

    @Value("${cos.region}")
    private String region;

    @Value("${cos.bucket}")
    private String bucket;

    @Value("${cos.secretId}")
    private String secretId;

    @Value("${cos.secretKey}")
    private String secretKey;

    /**
     * 获取临时凭证的配置
     */
    private final TreeMap<String, Object> config = new TreeMap<>();

    /**
     * 获取单一存储桶的临时凭证
     *
     * @return 临时凭证
     */
    public Response getCredential() {
        try {
            // 云 api 密钥 SecretId
            config.put("secretId", secretId);
            // 云 api 密钥 SecretKey
            config.put("secretKey", secretKey);
            // 临时密钥有效时长，单位是秒
            config.put("durationSeconds", 1800);
            config.put("bucket", bucket);
            config.put("region", region);
            // 密钥的权限列表。简单上传和分片需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            String[] allowActions = new String[]{
                    // 简单上传
                    "name/cos:PutObject",
                    "name/cos:PostObject",
                    // 分片上传
                    "name/cos:InitiateMultipartUpload",
                    "name/cos:ListMultipartUploads",
                    "name/cos:ListParts",
                    "name/cos:UploadPart",
                    "name/cos:CompleteMultipartUpload"
            };
            config.put("allowActions", allowActions);
            // 匹配所有文件
            config.put("allowPrefixes", new String[]{
                    "*"
            });
            return CosStsClient.getCredential(config);
        } catch (IOException e) {
            String msg = "获取临时密钥失败，原因是 %s".formatted(e.getMessage());
            log.error(msg);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, msg);
        }
    }
}
