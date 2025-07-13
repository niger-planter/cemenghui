package com.meeting.management.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meeting.management.entity.User;
import com.meeting.management.util.JsonFileStorage;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final JsonFileStorage<User> userStorage;

    @Autowired
    public UserController() {
        // 路径可根据实际情况调整
        this.userStorage = new JsonFileStorage<>("data/user.json", User.class);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userStorage.readAll();
            Optional<User> userOpt = users.stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst();
            if (userOpt.isPresent()) {
                result.put("success", true);
                result.put("data", userOpt.get());
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登录异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = userStorage.readAll();
            boolean exists = users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername()));
            if (exists) {
                result.put("success", false);
                result.put("message", "用户名已存在");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            users.add(user);
            userStorage.saveAll(users);
            result.put("success", true);
            result.put("data", user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "注册异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
} 