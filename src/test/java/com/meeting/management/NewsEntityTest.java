package com.meeting.management;

import com.meeting.management.entity.News;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Date;

public class NewsEntityTest {
    @Test
    void testGetterSetter() {
        News news = new News();
        news.setId(1L);
        news.setTitle("title");
        news.setAuthor("author");
        news.setCategory("cat");
        news.setSummary("sum");
        news.setContent("cont");
        news.setCoverImage("img");
        Date now = new Date();
        news.setPublishDate(now);
        news.setCreateTime(now);
        news.setUpdateTime(now);
        news.setStatus(1);
        news.setViewCount(10);
        Assertions.assertEquals(1L, news.getId());
        Assertions.assertEquals("title", news.getTitle());
        Assertions.assertEquals("author", news.getAuthor());
        Assertions.assertEquals("cat", news.getCategory());
        Assertions.assertEquals("sum", news.getSummary());
        Assertions.assertEquals("cont", news.getContent());
        Assertions.assertEquals("img", news.getCoverImage());
        Assertions.assertEquals(now, news.getPublishDate());
        Assertions.assertEquals(now, news.getCreateTime());
        Assertions.assertEquals(now, news.getUpdateTime());
        Assertions.assertEquals(1, news.getStatus());
        Assertions.assertEquals(10, news.getViewCount());
    }

    @Test
    void testEqualsAndHashCode() {
        News n1 = new News();
        n1.setId(1L);
        n1.setTitle("title");
        News n2 = new News();
        n2.setId(1L);
        n2.setTitle("title");
        Assertions.assertEquals(n1, n2);
        Assertions.assertEquals(n1.hashCode(), n2.hashCode());
        n2.setId(2L);
        Assertions.assertNotEquals(n1, n2);
    }

    @Test
    void testToStringNotNull() {
        News n = new News();
        n.setTitle("title");
        Assertions.assertNotNull(n.toString());
    }

    @Test
    void testEdgeCases() {
        News n = new News();
        n.setId(Long.MIN_VALUE);
        n.setTitle("");
        n.setAuthor(null);
        n.setCategory("");
        n.setSummary(null);
        n.setContent("");
        n.setCoverImage(null);
        n.setPublishDate(null);
        n.setCreateTime(null);
        n.setUpdateTime(null);
        n.setStatus(-1);
        n.setViewCount(Integer.MAX_VALUE);
        Assertions.assertEquals(Long.MIN_VALUE, n.getId());
        Assertions.assertEquals("", n.getTitle());
        Assertions.assertNull(n.getAuthor());
        Assertions.assertEquals("", n.getCategory());
        Assertions.assertNull(n.getSummary());
        Assertions.assertEquals("", n.getContent());
        Assertions.assertNull(n.getCoverImage());
        Assertions.assertNull(n.getPublishDate());
        Assertions.assertNull(n.getCreateTime());
        Assertions.assertNull(n.getUpdateTime());
        Assertions.assertEquals(-1, n.getStatus());
        Assertions.assertEquals(Integer.MAX_VALUE, n.getViewCount());
    }
} 