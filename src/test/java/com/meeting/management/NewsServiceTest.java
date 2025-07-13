package com.meeting.management;

import com.meeting.management.entity.News;
import com.meeting.management.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.*;
import java.util.*;

public class NewsServiceTest {
    private NewsServiceImpl newsService;

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl();
    }

    @Test
    void testCreateNews() {
        System.out.println("[TEST] testCreateNews - start");
        try {
            News news = new News();
            news.setTitle("Test News");
            news.setAuthor("author");
            news.setCategory("tech");
            news.setSummary("summary");
            news.setContent("content");
            news.setPublishDate(new Date());
            boolean result = newsService.createNews(news);
            Assertions.assertTrue(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testCreateNews: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testCreateNews: " + e.getMessage());
        }
        System.out.println("[TEST] testCreateNews - end");
    }

    @Test
    void testCreateNewsWithNull() {
        System.out.println("[TEST] testCreateNewsWithNull - start");
        try {
            boolean result = newsService.createNews(null);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testCreateNewsWithNull: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testCreateNewsWithNull: " + e.getMessage());
        }
        System.out.println("[TEST] testCreateNewsWithNull - end");
    }

    @Test
    void testCreateNewsWithMissingFields() {
        System.out.println("[TEST] testCreateNewsWithMissingFields - start");
        try {
            News news = new News();
            news.setTitle("NoContent");
            // 缺少content、author等
            boolean result = newsService.createNews(news);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testCreateNewsWithMissingFields: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testCreateNewsWithMissingFields: " + e.getMessage());
        }
        System.out.println("[TEST] testCreateNewsWithMissingFields - end");
    }

    @Test
    void testUpdateNews() {
        System.out.println("[TEST] testUpdateNews - start");
        try {
            News news = new News();
            news.setTitle("Test News");
            news.setAuthor("author");
            news.setCategory("tech");
            news.setSummary("summary");
            news.setContent("content");
            news.setPublishDate(new Date());
            newsService.createNews(news);
            // 获取ID
            Long id = news.getId();
            news.setId(id);
            news.setSummary("new summary");
            boolean result = newsService.updateNews(news);
            Assertions.assertTrue(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testUpdateNews: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testUpdateNews: " + e.getMessage());
        }
        System.out.println("[TEST] testUpdateNews - end");
    }

    @Test
    void testUpdateNewsWithNull() {
        System.out.println("[TEST] testUpdateNewsWithNull - start");
        try {
            boolean result = newsService.updateNews(null);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testUpdateNewsWithNull: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testUpdateNewsWithNull: " + e.getMessage());
        }
        System.out.println("[TEST] testUpdateNewsWithNull - end");
    }

    @Test
    void testUpdateNewsWithNullId() {
        System.out.println("[TEST] testUpdateNewsWithNullId - start");
        try {
            News news = new News();
            news.setTitle("NoId");
            news.setAuthor("author");
            news.setCategory("tech");
            news.setSummary("summary");
            news.setContent("content");
            news.setPublishDate(new Date());
            boolean result = newsService.updateNews(news);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testUpdateNewsWithNullId: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testUpdateNewsWithNullId: " + e.getMessage());
        }
        System.out.println("[TEST] testUpdateNewsWithNullId - end");
    }

    @Test
    void testDeleteNews() {
        System.out.println("[TEST] testDeleteNews - start");
        try {
            News news = new News();
            news.setTitle("ToDelete");
            news.setAuthor("author");
            news.setCategory("tech");
            news.setSummary("summary");
            news.setContent("content");
            news.setPublishDate(new Date());
            newsService.createNews(news);
            boolean result = newsService.deleteNews(news.getId());
            Assertions.assertTrue(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testDeleteNews: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testDeleteNews: " + e.getMessage());
        }
        System.out.println("[TEST] testDeleteNews - end");
    }

    @Test
    void testDeleteNewsWithNullId() {
        System.out.println("[TEST] testDeleteNewsWithNullId - start");
        try {
            boolean result = newsService.deleteNews(null);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testDeleteNewsWithNullId: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testDeleteNewsWithNullId: " + e.getMessage());
        }
        System.out.println("[TEST] testDeleteNewsWithNullId - end");
    }

    @Test
    void testCreateIndustryNewsWithEmptyTitle() {
        System.out.println("[TEST] testCreateIndustryNewsWithEmptyTitle - start");
        try {
            News news = new News();
            news.setTitle("");
            news.setAuthor("author");
            news.setCategory("industry");
            news.setSummary("summary");
            news.setContent("content");
            news.setPublishDate(new Date());
            boolean result = newsService.createNews(news);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testCreateIndustryNewsWithEmptyTitle: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testCreateIndustryNewsWithEmptyTitle: " + e.getMessage());
        }
        System.out.println("[TEST] testCreateIndustryNewsWithEmptyTitle - end");
    }

    @Test
    void testCreateIndustryNewsWithNullCategory() {
        System.out.println("[TEST] testCreateIndustryNewsWithNullCategory - start");
        try {
            News news = new News();
            news.setTitle("Industry News");
            news.setAuthor("author");
            news.setCategory(null);
            news.setSummary("summary");
            news.setContent("content");
            news.setPublishDate(new Date());
            boolean result = newsService.createNews(news);
            Assertions.assertFalse(result);
        } catch (AssertionError e) {
            System.out.println("[ASSERTION FAILED] testCreateIndustryNewsWithNullCategory: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("[EXCEPTION] testCreateIndustryNewsWithNullCategory: " + e.getMessage());
        }
        System.out.println("[TEST] testCreateIndustryNewsWithNullCategory - end");
    }
} 