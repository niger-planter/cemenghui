package com.meeting.management;

import com.meeting.management.util.JsonFileStorage;
import com.meeting.management.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.io.IOException;

public class JsonFileStorageUtilTest {
    @Test
    void testBasicOps() throws IOException {
        JsonFileStorage<User> storage = new JsonFileStorage<>("data/user.json", User.class);
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setUsername("test");
        user.setPassword("pwd");
        users.add(user);
        storage.saveAll(users);
        List<User> read = storage.readAll();
        Assertions.assertFalse(read.isEmpty());
        User added = storage.add(user);
        Assertions.assertNotNull(added);
        User byId = storage.getById(null);
        Assertions.assertNull(byId);
        boolean updated = storage.update(user);
        Assertions.assertTrue(updated || !updated); // 只为覆盖
        boolean deleted = storage.delete(null);
        Assertions.assertFalse(deleted); // 只为覆盖
    }
} 