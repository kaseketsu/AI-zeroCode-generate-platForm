package com.itflower.aiplatform.controller;

import com.itflower.aiplatform.common.response.BaseResponse;
import com.itflower.aiplatform.common.response.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Test {

    /**
     * 测试接口
     *
     * @return 字符串
     */
    @GetMapping("/hello")
    public BaseResponse<String> hello() {
        return ResultUtils.success("hello");
    }
}
