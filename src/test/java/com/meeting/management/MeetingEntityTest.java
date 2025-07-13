package com.meeting.management;

import com.meeting.management.entity.Meeting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Date;

public class MeetingEntityTest {
    @Test
    void testGetterSetter() {
        Meeting meeting = new Meeting();
        meeting.setId(1L);
        meeting.setMeetingName("name");
        meeting.setCreator("creator");
        Date now = new Date();
        meeting.setStartTime(now);
        meeting.setEndTime(now);
        meeting.setStatus(1);
        meeting.setContent("cont");
        meeting.setCoverImage("img");
        meeting.setCreateTime(now);
        meeting.setUpdateTime(now);
        meeting.setAuditStatus(1);
        meeting.setAuditRemark("remark");
        meeting.setCreatorType("user");
        Assertions.assertEquals(1L, meeting.getId());
        Assertions.assertEquals("name", meeting.getMeetingName());
        Assertions.assertEquals("creator", meeting.getCreator());
        Assertions.assertEquals(now, meeting.getStartTime());
        Assertions.assertEquals(now, meeting.getEndTime());
        Assertions.assertEquals(1, meeting.getStatus());
        Assertions.assertEquals("cont", meeting.getContent());
        Assertions.assertEquals("img", meeting.getCoverImage());
        Assertions.assertEquals(now, meeting.getCreateTime());
        Assertions.assertEquals(now, meeting.getUpdateTime());
        Assertions.assertEquals(1, meeting.getAuditStatus());
        Assertions.assertEquals("remark", meeting.getAuditRemark());
        Assertions.assertEquals("user", meeting.getCreatorType());
    }

    @Test
    void testEqualsAndHashCode() {
        Meeting m1 = new Meeting();
        m1.setId(1L);
        m1.setMeetingName("name");
        Meeting m2 = new Meeting();
        m2.setId(1L);
        m2.setMeetingName("name");
        Assertions.assertEquals(m1, m2);
        Assertions.assertEquals(m1.hashCode(), m2.hashCode());
        m2.setId(2L);
        Assertions.assertNotEquals(m1, m2);
    }

    @Test
    void testToStringNotNull() {
        Meeting m = new Meeting();
        m.setMeetingName("name");
        Assertions.assertNotNull(m.toString());
    }

    @Test
    void testEdgeCases() {
        Meeting m = new Meeting();
        m.setId(Long.MIN_VALUE);
        m.setMeetingName("");
        m.setCreator(null);
        m.setStartTime(null);
        m.setEndTime(null);
        m.setStatus(-1);
        m.setContent("");
        m.setCoverImage(null);
        m.setCreateTime(null);
        m.setUpdateTime(null);
        m.setAuditStatus(0);
        m.setAuditRemark(null);
        m.setCreatorType("");
        Assertions.assertEquals(Long.MIN_VALUE, m.getId());
        Assertions.assertEquals("", m.getMeetingName());
        Assertions.assertNull(m.getCreator());
        Assertions.assertNull(m.getStartTime());
        Assertions.assertNull(m.getEndTime());
        Assertions.assertEquals(-1, m.getStatus());
        Assertions.assertEquals("", m.getContent());
        Assertions.assertNull(m.getCoverImage());
        Assertions.assertNull(m.getCreateTime());
        Assertions.assertNull(m.getUpdateTime());
        Assertions.assertEquals(0, m.getAuditStatus());
        Assertions.assertNull(m.getAuditRemark());
        Assertions.assertEquals("", m.getCreatorType());
    }
} 