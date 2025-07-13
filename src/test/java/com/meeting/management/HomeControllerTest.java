package com.meeting.management;

import com.meeting.management.controller.HomeController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HomeControllerTest {
    @Test
    void testHome() {
        HomeController controller = new HomeController();
        String resp = controller.home();
        Assertions.assertNotNull(resp);
        Assertions.assertTrue(resp.contains("Meeting Management API"));
    }
} 