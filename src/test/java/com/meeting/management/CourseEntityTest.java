package com.meeting.management;

import com.meeting.management.entity.Course;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Date;

public class CourseEntityTest {
    @Test
    void testGetterSetter() {
        Course course = new Course();
        course.setId(1L);
        course.setCourseName("name");
        course.setCoverImage("img");
        course.setDescription("desc");
        course.setOrderNum(2);
        course.setVideoUrl("url");
        course.setAuthor("author");
        course.setStatus(1);
        Date now = new Date();
        course.setCreateTime(now);
        course.setUpdateTime(now);
        course.setAuditStatus(1);
        course.setAuditRemark("remark");
        Assertions.assertEquals(1L, course.getId());
        Assertions.assertEquals("name", course.getCourseName());
        Assertions.assertEquals("img", course.getCoverImage());
        Assertions.assertEquals("desc", course.getDescription());
        Assertions.assertEquals(2, course.getOrderNum());
        Assertions.assertEquals("url", course.getVideoUrl());
        Assertions.assertEquals("author", course.getAuthor());
        Assertions.assertEquals(1, course.getStatus());
        Assertions.assertEquals(now, course.getCreateTime());
        Assertions.assertEquals(now, course.getUpdateTime());
        Assertions.assertEquals(1, course.getAuditStatus());
        Assertions.assertEquals("remark", course.getAuditRemark());
    }

    @Test
    void testEqualsAndHashCode() {
        Course c1 = new Course();
        c1.setId(1L);
        c1.setCourseName("name");
        Course c2 = new Course();
        c2.setId(1L);
        c2.setCourseName("name");
        Assertions.assertEquals(c1, c2);
        Assertions.assertEquals(c1.hashCode(), c2.hashCode());
        c2.setId(2L);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test
    void testToStringNotNull() {
        Course c = new Course();
        c.setCourseName("name");
        Assertions.assertNotNull(c.toString());
    }

    @Test
    void testEdgeCases() {
        Course c = new Course();
        c.setId(Long.MAX_VALUE);
        c.setCourseName("");
        c.setCoverImage(null);
        c.setDescription("");
        c.setOrderNum(Integer.MIN_VALUE);
        c.setVideoUrl(null);
        c.setAuthor("");
        c.setStatus(-1);
        c.setCreateTime(null);
        c.setUpdateTime(null);
        c.setAuditStatus(0);
        c.setAuditRemark(null);
        Assertions.assertEquals(Long.MAX_VALUE, c.getId());
        Assertions.assertEquals("", c.getCourseName());
        Assertions.assertNull(c.getCoverImage());
        Assertions.assertEquals("", c.getDescription());
        Assertions.assertEquals(Integer.MIN_VALUE, c.getOrderNum());
        Assertions.assertNull(c.getVideoUrl());
        Assertions.assertEquals("", c.getAuthor());
        Assertions.assertEquals(-1, c.getStatus());
        Assertions.assertNull(c.getCreateTime());
        Assertions.assertNull(c.getUpdateTime());
        Assertions.assertEquals(0, c.getAuditStatus());
        Assertions.assertNull(c.getAuditRemark());
    }
} 