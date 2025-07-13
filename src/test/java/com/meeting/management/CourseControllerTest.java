package com.meeting.management;

import com.meeting.management.controller.CourseController;
import com.meeting.management.entity.Course;
import com.meeting.management.service.CourseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Field;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import static org.mockito.Mockito.*;

public class CourseControllerTest {
    @Test
    void testCreateCourseWithEmpty() throws Exception {
        CourseController controller = new CourseController();
        // 注入mock CourseService
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.createCourse(Mockito.any())).thenReturn(false);
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Course course = new Course();
        ResponseEntity<Map<String, Object>> resp = controller.createCourse(course);
        Assertions.assertNotNull(resp);
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testCreateCourseSuccess() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.createCourse(Mockito.any())).thenReturn(true);
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Course course = new Course();
        course.setCourseName("c");
        course.setAuthor("a");
        ResponseEntity<Map<String, Object>> resp = controller.createCourse(course);
        Assertions.assertTrue((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUpdateCourseNotFound() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.updateCourse(Mockito.any())).thenReturn(false);
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Course course = new Course();
        course.setId(999L);
        ResponseEntity<Map<String, Object>> resp = controller.updateCourse(999L, course);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testDeleteCourseNotFound() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.deleteCourse(Mockito.anyLong())).thenReturn(false);
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        ResponseEntity<Map<String, Object>> resp = controller.deleteCourse(999L);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testGetCourseDetailNotFound() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.getCourseById(Mockito.anyLong())).thenReturn(null);
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        ResponseEntity<Map<String, Object>> resp = controller.getCourseDetail(999L);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testSearchCoursesNormal() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.searchCourses(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(java.util.Collections.emptyList());
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        ResponseEntity<Map<String, Object>> resp = controller.searchCourses("name", "author", 1, 10);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
        Assertions.assertNotNull(resp.getBody().get("records"));
    }

    @Test
    void testSearchCoursesWithNullParams() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.searchCourses(Mockito.anyMap(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(java.util.Collections.emptyList());
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        ResponseEntity<Map<String, Object>> resp = controller.searchCourses(null, null, 1, 10);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    void testCreateCourseServiceException() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.createCourse(Mockito.any())).thenThrow(new RuntimeException("mock error"));
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Course course = new Course();
        ResponseEntity<Map<String, Object>> resp = controller.createCourse(course);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUpdateCourseServiceException() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.updateCourse(Mockito.any())).thenThrow(new RuntimeException("mock error"));
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Course course = new Course();
        ResponseEntity<Map<String, Object>> resp = controller.updateCourse(1L, course);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testDeleteCourseServiceException() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.deleteCourse(Mockito.anyLong())).thenThrow(new RuntimeException("mock error"));
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        ResponseEntity<Map<String, Object>> resp = controller.deleteCourse(1L);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testGetCourseDetailServiceException() throws Exception {
        CourseController controller = new CourseController();
        CourseService mockService = Mockito.mock(CourseService.class);
        Mockito.when(mockService.getCourseById(Mockito.anyLong())).thenThrow(new RuntimeException("mock error"));
        Field field = CourseController.class.getDeclaredField("courseService");
        field.setAccessible(true);
        field.set(controller, mockService);
        ResponseEntity<Map<String, Object>> resp = controller.getCourseDetail(1L);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUploadCoverImageEmpty() {
        CourseController controller = new CourseController();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUploadCoverImageFormatError() {
        CourseController controller = new CourseController();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUploadCoverImageIOException() throws Exception {
        CourseController controller = new CourseController();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("test.png");
        doThrow(new java.io.IOException("mock io error")).when(file).transferTo(Mockito.any(java.io.File.class));
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUploadCoverImageWithLongFileName() {
        CourseController controller = new CourseController();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("a".repeat(300) + ".png");
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertTrue(resp.getStatusCode() == HttpStatus.OK || resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 