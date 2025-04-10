package com.lsh.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@Tag(name = "示例接口", description = "演示API接口")
public class DemoController {

    @Operation(summary = "Hello接口", description = "返回world字符串的简单示例接口")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功返回world")
    })
    @GetMapping("/hello")
    public String hello(){
        return "world";
    }
}
