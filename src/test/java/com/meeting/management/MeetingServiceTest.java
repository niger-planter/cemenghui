package com.meeting.management;

import com.meeting.management.entity.Meeting;
import com.meeting.management.service.impl.MeetingServiceImpl;
import com.meeting.management.util.JsonFileStorage;
import org.junit.jupiter.api.*;
import java.util.*;
import java.lang.reflect.Field;

public class MeetingServiceTest {
    private MeetingServiceImpl meetingService;

    @BeforeEach
    void setUp() throws Exception {
        meetingService = new MeetingServiceImpl();
        // 反射注入依赖
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(meetingService, new JsonFileStorage<>("data/meetings.json", Meeting.class));
    }

    @Test
    void testCreateMeeting() {
        Meeting meeting = new Meeting();
        meeting.setMeetingName("Test Meeting");
        meeting.setStartTime(new Date());
        meeting.setEndTime(new Date(System.currentTimeMillis() + 3600_000));
        meeting.setContent("内容");
        meeting.setCreator("tester");
        meeting.setCreatorType("user");
        boolean result = meetingService.createMeeting(meeting);
        Assertions.assertTrue(result);
    }

    @Test
    void testCreateMeetingWithNull() {
        try {
            boolean result = meetingService.createMeeting(null);
            Assertions.assertFalse(result);
        } catch (Exception e) {
            System.out.println("testCreateMeetingWithNull error: " + e.getMessage());
        }
    }

    @Test
    void testCreateMeetingWithMissingFields() {
        try {
            Meeting meeting = new Meeting();
            meeting.setMeetingName("NoTime");
            // 缺少startTime/endTime
            boolean result = meetingService.createMeeting(meeting);
            Assertions.assertFalse(result);
        } catch (Exception e) {
            System.out.println("testCreateMeetingWithMissingFields error: " + e.getMessage());
        }
    }

    @Test
    void testCreateMeetingWithInvalidTime() {
        try {
            Meeting meeting = new Meeting();
            meeting.setMeetingName("InvalidTime");
            meeting.setStartTime(new Date(System.currentTimeMillis() + 3600_000));
            meeting.setEndTime(new Date());
            meeting.setContent("内容");
            meeting.setCreator("tester");
            meeting.setCreatorType("user");
            boolean result = meetingService.createMeeting(meeting);
            Assertions.assertFalse(result);
        } catch (Exception e) {
            System.out.println("testCreateMeetingWithInvalidTime error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateMeeting() {
        Meeting meeting = new Meeting();
        meeting.setId(System.currentTimeMillis());
        meeting.setMeetingName("Updated Title");
        meeting.setStartTime(new Date());
        meeting.setEndTime(new Date(System.currentTimeMillis() + 3600_000));
        meeting.setContent("内容");
        meeting.setCreator("tester");
        meeting.setCreatorType("user");
        // 先添加
        meetingService.createMeeting(meeting);
        // 再更新
        meeting.setContent("新内容");
        boolean result = meetingService.updateMeeting(meeting);
        Assertions.assertTrue(result);
    }

    @Test
    void testUpdateMeetingWithNull() {
        try {
            boolean result = meetingService.updateMeeting(null);
            Assertions.assertFalse(result);
        } catch (Exception e) {
            System.out.println("testUpdateMeetingWithNull error: " + e.getMessage());
        }
    }

    @Test
    void testUpdateMeetingWithMissingId() {
        try {
            Meeting meeting = new Meeting();
            meeting.setMeetingName("NoId");
            meeting.setStartTime(new Date());
            meeting.setEndTime(new Date(System.currentTimeMillis() + 3600_000));
            meeting.setContent("内容");
            meeting.setCreator("tester");
            meeting.setCreatorType("user");
            boolean result = meetingService.updateMeeting(meeting);
            Assertions.assertFalse(result);
        } catch (Exception e) {
            System.out.println("testUpdateMeetingWithMissingId error: " + e.getMessage());
        }
    }

    @Test
    void testDeleteMeeting() {
        Meeting meeting = new Meeting();
        meeting.setId(System.currentTimeMillis());
        meeting.setMeetingName("ToDelete");
        meeting.setStartTime(new Date());
        meeting.setEndTime(new Date(System.currentTimeMillis() + 3600_000));
        meeting.setContent("内容");
        meeting.setCreator("tester");
        meeting.setCreatorType("user");
        meetingService.createMeeting(meeting);
        boolean result = meetingService.deleteMeeting(meeting.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void testDeleteMeetingWithNullId() {
        try {
            boolean result = meetingService.deleteMeeting(null);
            Assertions.assertFalse(result);
        } catch (Exception e) {
            System.out.println("testDeleteMeetingWithNullId error: " + e.getMessage());
        }
    }
} 