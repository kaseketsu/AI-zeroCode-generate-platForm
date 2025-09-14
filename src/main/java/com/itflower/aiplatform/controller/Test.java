package com.itflower.aiplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {

    /**
     * 测试接口
     *
     * @return 字符串
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
