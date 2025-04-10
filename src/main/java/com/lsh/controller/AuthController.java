package com.lsh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 认证控制器
 * 提供JWT认证相关的API接口
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "JWT认证相关的API接口")
public class AuthController {

    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 登录响应DTO
     */
    public static class LoginResponse {
        private String token;
        private String type = "Bearer";

        public LoginResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * 用户登录接口
     *
     * @param loginRequest 登录请求
     * @return JWT令牌
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户凭据并返回JWT令牌")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功", 
                   content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 这里应该有实际的用户验证逻辑
        // 为了演示，我们简单地检查用户名和密码是否为admin/admin
        if ("admin".equals(loginRequest.getUsername()) && 
            "admin".equals(loginRequest.getPassword())) {
            // 生成一个模拟的JWT令牌
            String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";
            return ResponseEntity.ok(new LoginResponse(jwtToken));
        }
        
        return ResponseEntity.status(401).body("用户名或密码错误");
    }
}