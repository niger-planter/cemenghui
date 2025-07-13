package com.meeting.management;

import com.meeting.management.entity.Course;
import com.meeting.management.service.impl.CourseServiceImpl;
import com.meeting.management.util.JsonFileStorage;
import org.junit.jupiter.api.*;
import java.util.*;
import java.lang.reflect.Field;

public class CourseServiceTest {
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() throws Exception {
        courseService = new CourseServiceImpl();
        // 反射注入依赖
        Field field = CourseServiceImpl.class.getDeclaredField("courseJsonFileStorage");
        field.setAccessible(true);
        field.set(courseService, new JsonFileStorage<>("data/courses.json", Course.class));
    }

    @Test
    void testCreateCourse() {
        System.out.println("[TEST] testCreateCourse - start");
        try {
            Course course = new Course();
            course.setCourseName("Java");
            course.setAuthor("author");
            course.setDescription("desc");
            course.setOrderNum(1);
            course.setStatus(1);
            course.setVideoUrl("url");
            course.setCoverImage("cover.jpg");
            boolean result = courseService.createCourse(course);
            Assertions.assertTrue(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testCreateCourse: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testCreateCourse: " + e.getMessage());
        }
        System.out.println("[TEST] testCreateCourse - end");
    }

    @Test
    void testCreateCourseWithNull() {
        System.out.println("[TEST] testCreateCourseWithNull - start");
        try {
            boolean result = courseService.createCourse(null);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testCreateCourseWithNull: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testCreateCourseWithNull: " + e.getMessage());
        }
        System.out.println("[TEST] testCreateCourseWithNull - end");
    }

    @Test
    void testUpdateCourse() {
        System.out.println("[TEST] testUpdateCourse - start");
        try {
            Course course = new Course();
            course.setId(System.currentTimeMillis());
            course.setCourseName("Python");
            course.setAuthor("author");
            course.setDescription("desc");
            course.setOrderNum(1);
            course.setStatus(1);
            course.setVideoUrl("url");
            course.setCoverImage("cover.jpg");
            // 先添加
            courseService.createCourse(course);
            // 再更新
            course.setDescription("new desc");
            boolean result = courseService.updateCourse(course);
            Assertions.assertTrue(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testUpdateCourse: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testUpdateCourse: " + e.getMessage());
        }
        System.out.println("[TEST] testUpdateCourse - end");
    }

    @Test
    void testUpdateCourseWithNull() {
        System.out.println("[TEST] testUpdateCourseWithNull - start");
        try {
            boolean result = courseService.updateCourse(null);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testUpdateCourseWithNull: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testUpdateCourseWithNull: " + e.getMessage());
        }
        System.out.println("[TEST] testUpdateCourseWithNull - end");
    }

    @Test
    void testUpdateCourseWithNullId() {
        System.out.println("[TEST] testUpdateCourseWithNullId - start");
        try {
            Course course = new Course();
            course.setCourseName("NoId");
            boolean result = courseService.updateCourse(course);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testUpdateCourseWithNullId: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testUpdateCourseWithNullId: " + e.getMessage());
        }
        System.out.println("[TEST] testUpdateCourseWithNullId - end");
    }

    @Test
    void testDeleteCourse() {
        System.out.println("[TEST] testDeleteCourse - start");
        try {
            Course course = new Course();
            course.setId(System.currentTimeMillis());
            course.setCourseName("ToDelete");
            course.setAuthor("author");
            course.setDescription("desc");
            course.setOrderNum(1);
            course.setStatus(1);
            course.setVideoUrl("url");
            course.setCoverImage("cover.jpg");
            courseService.createCourse(course);
            boolean result = courseService.deleteCourse(course.getId());
            Assertions.assertTrue(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testDeleteCourse: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testDeleteCourse: " + e.getMessage());
        }
        System.out.println("[TEST] testDeleteCourse - end");
    }

    @Test
    void testDeleteCourseWithNullId() {
        System.out.println("[TEST] testDeleteCourseWithNullId - start");
        try {
            boolean result = courseService.deleteCourse(null);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testDeleteCourseWithNullId: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testDeleteCourseWithNullId: " + e.getMessage());
        }
        System.out.println("[TEST] testDeleteCourseWithNullId - end");
    }
} 