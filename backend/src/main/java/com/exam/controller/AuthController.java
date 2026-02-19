package com.exam.controller;

import com.exam.entity.User;
import com.exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = userService.login(username, password);
        return result;
    }
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        userService.register(user);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "注册成功");
        return result;
    }
    
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestHeader("Authorization") String token) {
        // 从token解析用户信息（简化处理）
        return new HashMap<>();
    }
}
