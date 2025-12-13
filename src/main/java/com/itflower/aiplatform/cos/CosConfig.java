package com.itflower.aiplatform.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.region.Region;
import com.tencent.cloud.Response;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CosConfig {

    @Value("${cos.region}")
    private String region;

    @Resource
    private CosCredential cosCredential;

    @Bean
    public COSClient cosClient() {
        Response credential = cosCredential.getCredential();
        String tmpSecretId = credential.credentials.tmpSecretId;
        String tmpSecretKey = credential.credentials.tmpSecretKey;
        String sessionToken = credential.credentials.sessionToken;
        BasicSessionCredentials cred = new BasicSessionCredentials(tmpSecretId, tmpSecretKey, sessionToken);
        // 设置 bucket 的地域
        String regionName = region;
        Region region = new Region(regionName);
        ClientConfig clientConfig = new ClientConfig(region);
        // 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }

}