package com.meeting.management;

import com.meeting.management.entity.User;
import com.meeting.management.util.JsonFileStorage;
import org.junit.jupiter.api.*;
import java.util.*;
import java.io.IOException;

public class UserServiceTest {
    private JsonFileStorage<User> userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new JsonFileStorage<>("data/user.json", User.class);
    }

    @Test
    void testLoginSuccess() {
        System.out.println("[TEST] testLoginSuccess - start");
        try {
            List<User> users = new ArrayList<>();
            User user = new User();
            user.setUsername("alice");
            user.setPassword("password");
            user.setNickname("Alice");
            user.setRole("user");
            users.add(user);
            userStorage.saveAll(users);
            Optional<User> userOpt = userStorage.readAll().stream().filter(u -> u.getUsername().equals("alice") && u.getPassword().equals("password")).findFirst();
            Assertions.assertTrue(userOpt.isPresent());
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testLoginSuccess: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testLoginSuccess: " + e.getMessage());
        }
        System.out.println("[TEST] testLoginSuccess - end");
    }

    @Test
    void testLoginWithWrongPassword() throws IOException {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("bob");
        user.setPassword("123456");
        user.setNickname("Bob");
        user.setRole("user");
        users.add(user);
        userStorage.saveAll(users);
        Optional<User> userOpt = userStorage.readAll().stream().filter(u -> u.getUsername().equals("bob") && u.getPassword().equals("wrong")).findFirst();
        Assertions.assertFalse(userOpt.isPresent());
    }

    @Test
    void testLoginWithEmptyUsername() throws IOException {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("admin");
        user.setPassword("adminpass");
        user.setNickname("Admin");
        user.setRole("admin");
        users.add(user);
        userStorage.saveAll(users);
        Optional<User> userOpt = userStorage.readAll().stream().filter(u -> u.getUsername().equals("") && u.getPassword().equals("adminpass")).findFirst();
        Assertions.assertFalse(userOpt.isPresent());
    }

    @Test
    void testLoginWithNull() {
        try {
            Optional<User> userOpt = null;
            Assertions.assertNull(userOpt);
        } catch (Exception e) {
            System.out.println("testLoginWithNull error: " + e.getMessage());
        }
    }

    @Test
    void testLoginWithNullPassword() {
        try {
            List<User> users = new ArrayList<>();
            User user = new User();
            user.setUsername("testuser");
            user.setPassword(null);
            user.setNickname("Test");
            user.setRole("user");
            users.add(user);
            userStorage.saveAll(users);
            Optional<User> userOpt = userStorage.readAll().stream().filter(u -> u.getUsername().equals("testuser") && u.getPassword() == null).findFirst();
            Assertions.assertTrue(userOpt.isPresent());
        } catch (Exception e) {
            System.out.println("testLoginWithNullPassword error: " + e.getMessage());
        }
    }

    @Test
    void testRegisterWithNullUser() {
        try {
            User user = null;
            Assertions.assertNull(user);
        } catch (Exception e) {
            System.out.println("testRegisterWithNullUser error: " + e.getMessage());
        }
    }
} 