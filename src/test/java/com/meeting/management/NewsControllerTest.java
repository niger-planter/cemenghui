package com.meeting.management;

import com.meeting.management.controller.NewsController;
import com.meeting.management.entity.News;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.meeting.management.service.NewsService;
import java.lang.reflect.Field;
import static org.mockito.Mockito.*;

public class NewsControllerTest {
    private NewsController buildControllerWithMock(NewsService mockService) throws Exception {
        NewsController controller = new NewsController();
        Field field = NewsController.class.getDeclaredField("newsService");
        field.setAccessible(true);
        field.set(controller, mockService);
        return controller;
    }

    @Test
    void testCreateNewsWithEmpty() throws Exception {
        NewsService mockService = mock(NewsService.class);
        when(mockService.createNews(any())).thenReturn(false);
        NewsController controller = buildControllerWithMock(mockService);
        News news = new News();
        ResponseEntity<Map<String, Object>> resp = controller.createNews(news);
        Assertions.assertNotNull(resp);
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testCreateNewsSuccess() throws Exception {
        NewsService mockService = mock(NewsService.class);
        when(mockService.createNews(any())).thenReturn(true);
        NewsController controller = buildControllerWithMock(mockService);
        News news = new News();
        news.setTitle("title");
        news.setContent("content");
        news.setCategory("cat");
        ResponseEntity<Map<String, Object>> resp = controller.createNews(news);
        Assertions.assertTrue((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUpdateNewsNotFound() throws Exception {
        NewsService mockService = mock(NewsService.class);
        when(mockService.updateNews(any())).thenReturn(false);
        NewsController controller = buildControllerWithMock(mockService);
        News news = new News();
        news.setId(999L);
        ResponseEntity<Map<String, Object>> resp = controller.updateNews(999L, news);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testDeleteNewsNotFound() throws Exception {
        NewsService mockService = mock(NewsService.class);
        when(mockService.deleteNews(anyLong())).thenReturn(false);
        NewsController controller = buildControllerWithMock(mockService);
        ResponseEntity<Map<String, Object>> resp = controller.deleteNews(999L);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testGetNewsByIdNotFound() throws Exception {
        NewsService mockService = mock(NewsService.class);
        when(mockService.getNewsById(anyLong())).thenReturn(null);
        NewsController controller = buildControllerWithMock(mockService);
        ResponseEntity<Map<String, Object>> resp = controller.getNewsById(999L);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUploadCoverImageEmpty() throws Exception {
        NewsService mockService = mock(NewsService.class);
        NewsController controller = buildControllerWithMock(mockService);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }

    @Test
    void testUploadCoverImageFormatError() throws Exception {
        NewsService mockService = mock(NewsService.class);
        NewsController controller = buildControllerWithMock(mockService);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }
} 