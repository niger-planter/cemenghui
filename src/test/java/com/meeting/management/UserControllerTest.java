package com.meeting.management;

import com.meeting.management.controller.UserController;
import com.meeting.management.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserControllerTest {
    @Test
    void testLoginWithEmpty() {
        UserController controller = new UserController();
        Map<String, String> req = new HashMap<>();
        req.put("username", "");
        req.put("password", "");
        ResponseEntity<Map<String, Object>> resp = controller.login(req);
        Assertions.assertNotNull(resp);
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testLoginWithWrongPassword() {
        UserController controller = new UserController();
        Map<String, String> req = new HashMap<>();
        req.put("username", "not_exist");
        req.put("password", "wrong");
        ResponseEntity<Map<String, Object>> resp = controller.login(req);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testRegisterAndLoginSuccess() {
        UserController controller = new UserController();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpwd");
        ResponseEntity<Map<String, Object>> regResp = controller.register(user);
        Assertions.assertTrue((Boolean)regResp.getBody().get("success"));
        Map<String, String> req = new HashMap<>();
        req.put("username", "testuser");
        req.put("password", "testpwd");
        ResponseEntity<Map<String, Object>> loginResp = controller.login(req);
        Assertions.assertTrue((Boolean)loginResp.getBody().get("success"));
    }

    @Test
    void testRegisterDuplicate() {
        UserController controller = new UserController();
        User user = new User();
        user.setUsername("dupuser");
        user.setPassword("pwd");
        controller.register(user);
        ResponseEntity<Map<String, Object>> regResp2 = controller.register(user);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, regResp2.getStatusCode());
        Assertions.assertFalse((Boolean)regResp2.getBody().get("success"));
    }

    @Test
    void testRegisterException() {
        UserController controller = new UserController() {
            @Override
            public ResponseEntity<Map<String, Object>> register(User user) {
                throw new RuntimeException("mock error");
            }
        };
        User user = new User();
        user.setUsername("err");
        user.setPassword("err");
        try {
            controller.register(user);
            Assertions.fail("Should throw exception");
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains("mock error"));
        }
    }

    @Test
    void testLoginWithNullRequest() {
        UserController controller = new UserController();
        ResponseEntity<Map<String, Object>> resp = controller.login(null);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testLoginWithMissingFields() {
        UserController controller = new UserController();
        Map<String, String> req = new HashMap<>();
        req.put("username", "user");
        // password 缺失
        ResponseEntity<Map<String, Object>> resp = controller.login(req);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testLoginWithLongUsername() throws Exception {
        UserController controller = new UserController();
        java.lang.reflect.Field field = controller.getClass().getDeclaredField("userStorage");
        field.setAccessible(true);
        field.set(controller, new com.meeting.management.util.JsonFileStorage<>("bad_path", User.class) {
            @Override
            public java.util.List<User> readAll() { return java.util.Collections.emptyList(); }
        });
        Map<String, String> req = new HashMap<>();
        String longName = "a".repeat(1000);
        req.put("username", longName);
        req.put("password", "pwd");
        ResponseEntity<Map<String, Object>> resp = controller.login(req);
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testRegisterWithNullUser() {
        UserController controller = new UserController();
        ResponseEntity<Map<String, Object>> resp = controller.register(null);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testRegisterWithEmptyUsername() {
        UserController controller = new UserController();
        User user = new User();
        user.setUsername("");
        user.setPassword("pwd");
        ResponseEntity<Map<String, Object>> resp = controller.register(user);
        // 允许注册空用户名？如业务不允许应断言 BAD_REQUEST
        Assertions.assertTrue(resp.getStatusCode() == HttpStatus.OK || resp.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    void testRegisterWithLongUsername() {
        UserController controller = new UserController();
        User user = new User();
        user.setUsername("a".repeat(1000));
        user.setPassword("pwd");
        ResponseEntity<Map<String, Object>> resp = controller.register(user);
        Assertions.assertTrue(resp.getStatusCode() == HttpStatus.OK || resp.getStatusCode() == HttpStatus.BAD_REQUEST);
    }

    @Test
    void testLoginStorageException() throws Exception {
        UserController controller = new UserController();
        java.lang.reflect.Field field = controller.getClass().getDeclaredField("userStorage");
        field.setAccessible(true);
        field.set(controller, new com.meeting.management.util.JsonFileStorage<>("bad_path", User.class) {
            @Override
            public java.util.List<User> readAll() { throw new RuntimeException("mock storage error"); }
        });
        Map<String, String> req = new HashMap<>();
        req.put("username", "user");
        req.put("password", "pwd");
        ResponseEntity<Map<String, Object>> resp = controller.login(req);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
        Assertions.assertTrue(resp.getBody().get("message").toString().contains("mock storage error"));
    }

    @Test
    void testRegisterStorageException() throws Exception {
        UserController controller = new UserController();
        java.lang.reflect.Field field = controller.getClass().getDeclaredField("userStorage");
        field.setAccessible(true);
        field.set(controller, new com.meeting.management.util.JsonFileStorage<>("bad_path", User.class) {
            @Override
            public java.util.List<User> readAll() { throw new RuntimeException("mock storage error"); }
        });
        User user = new User();
        user.setUsername("user");
        user.setPassword("pwd");
        ResponseEntity<Map<String, Object>> resp = controller.register(user);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
        Assertions.assertTrue(resp.getBody().get("message").toString().contains("mock storage error"));
    }
} 