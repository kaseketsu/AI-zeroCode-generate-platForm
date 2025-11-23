package com.itflower.aiplatform;

import com.itflower.aiplatform.ai.AiRoutingService;
import com.itflower.aiplatform.model.enums.GenTypeEnums;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiPlatformApplicationTests {


    @Resource
    private AiRoutingService aiRoutingService;

    @Test
    void contextLoads() {
        String prompt = "做一个简单的个人网站";
        GenTypeEnums enums = aiRoutingService.aiRouting(prompt);
        System.out.println(enums.getValue());
        prompt = "做一个公司网站，需要首页、关于我们、联系我们三个页面";
        enums = aiRoutingService.aiRouting(prompt);
        System.out.println(enums.getValue());
        prompt = "做一个电商网站，包含用户管理、商品管理、订单管理，需要路由和状态管理";
        enums = aiRoutingService.aiRouting(prompt);
        System.out.println(enums.getValue());
    }

}
