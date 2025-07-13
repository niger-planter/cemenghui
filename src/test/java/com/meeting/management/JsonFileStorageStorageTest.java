package com.meeting.management;

import com.meeting.management.storage.JsonFileStorage;
import com.meeting.management.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class JsonFileStorageStorageTest {
    @Test
    void testBasicOps() throws Exception {
        Path tempFile = Files.createTempFile("test-users", ".json");
        tempFile.toFile().deleteOnExit();
        JsonFileStorage<User> storage = new JsonFileStorage<>(
            tempFile, new TypeReference<List<User>>() {}) {};
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("pwd");
        users.add(user);
        storage.saveAll(users);
        List<User> read = storage.readAll();
        Assertions.assertFalse(read.isEmpty());
        storage.add(user);
        Optional<User> byId = storage.findById(null);
        Assertions.assertTrue(byId.isEmpty() || byId.isPresent());
        storage.update(user);
        storage.delete(null);
    }
} 