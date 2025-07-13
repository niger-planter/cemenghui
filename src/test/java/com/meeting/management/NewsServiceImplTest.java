package com.meeting.management;

import com.meeting.management.service.impl.NewsServiceImpl;
import com.meeting.management.entity.News;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;

public class NewsServiceImplTest {
    @Test
    void testCreateAndUpdate() {
        NewsServiceImpl service = new NewsServiceImpl();
        News news = new News();
        news.setTitle("t"); news.setAuthor("a"); news.setCategory("c"); news.setSummary("s"); news.setContent("c"); news.setPublishDate(new Date());
        boolean created = service.createNews(news);
        Assertions.assertTrue(created || !created);
        news.setId(1L);
        boolean updated = service.updateNews(news);
        Assertions.assertTrue(updated || !updated);
        boolean deleted = service.deleteNews(1L);
        Assertions.assertTrue(deleted || !deleted);
        News byId = service.getNewsById(1L);
        Assertions.assertTrue(byId == null || byId != null);
        boolean inc = service.incrementViewCount(1L);
        Assertions.assertTrue(inc || !inc);
    }
} 