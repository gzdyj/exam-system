package com.exam.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.User;
import com.exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public Map<String, Object> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer role) {
        Page<User> pageResult = userService.getUserPage(page, limit, username, role);
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        return result;
    }
    
    @PostMapping
    public Map<String, Object> addUser(@RequestBody User user) {
        userService.addUser(user);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "添加成功");
        return result;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "更新成功");
        return result;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("message", "删除成功");
        return result;
    }
}
