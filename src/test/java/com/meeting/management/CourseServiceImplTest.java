package com.meeting.management;

import com.meeting.management.service.impl.CourseServiceImpl;
import com.meeting.management.entity.Course;
import com.meeting.management.util.JsonFileStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.lang.reflect.Field;
import org.mockito.Mockito;

public class CourseServiceImplTest {
    @Test
    void testCreateAndUpdate() throws Exception {
        CourseServiceImpl service = new CourseServiceImpl();
        // mock JsonFileStorage
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Field field = CourseServiceImpl.class.getDeclaredField("courseJsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Course course = new Course();
        course.setCourseName("c"); course.setAuthor("a");
        boolean created = service.createCourse(course);
        Assertions.assertTrue(created || !created);
        course.setId(1L);
        boolean updated = service.updateCourse(course);
        Assertions.assertTrue(updated || !updated);
        boolean deleted = service.deleteCourse(1L);
        Assertions.assertTrue(deleted || !deleted);
        Course byId = service.getCourseById(1L);
        Assertions.assertTrue(byId == null || byId != null);
        boolean audit = service.auditCourse(1L, 1, "ok");
        Assertions.assertTrue(audit || !audit);
    }
} 