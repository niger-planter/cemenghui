package com.meeting.management;

import com.meeting.management.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserEntityTest {
    @Test
    void testGetterSetter() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("pwd");
        user.setNickname("nick");
        user.setRole("admin");
        user.setEnabled(false);
        Assertions.assertEquals("test", user.getUsername());
        Assertions.assertEquals("pwd", user.getPassword());
        Assertions.assertEquals("nick", user.getNickname());
        Assertions.assertEquals("admin", user.getRole());
        Assertions.assertFalse(user.getEnabled());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setUsername("test");
        user1.setPassword("pwd");
        user1.setNickname("nick");
        user1.setRole("admin");
        user1.setEnabled(true);
        User user2 = new User();
        user2.setUsername("test");
        user2.setPassword("pwd");
        user2.setNickname("nick");
        user2.setRole("admin");
        user2.setEnabled(true);
        Assertions.assertEquals(user1, user2);
        Assertions.assertEquals(user1.hashCode(), user2.hashCode());
        user2.setUsername("diff");
        Assertions.assertNotEquals(user1, user2);
    }

    @Test
    void testToStringNotNull() {
        User user = new User();
        user.setUsername("test");
        Assertions.assertNotNull(user.toString());
    }

    @Test
    void testEdgeCases() {
        User user = new User();
        user.setUsername(null);
        user.setPassword("");
        user.setNickname(null);
        user.setRole("");
        user.setEnabled(false);
        Assertions.assertNull(user.getUsername());
        Assertions.assertEquals("", user.getPassword());
        Assertions.assertNull(user.getNickname());
        Assertions.assertEquals("", user.getRole());
        Assertions.assertFalse(user.getEnabled());
    }
} 