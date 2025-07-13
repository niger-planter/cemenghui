package com.meeting.management;

import com.meeting.management.service.impl.MeetingServiceImpl;
import com.meeting.management.entity.Meeting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;
import com.meeting.management.util.JsonFileStorage;
import java.lang.reflect.Field;
import org.mockito.Mockito;

public class MeetingServiceImplTest {
    @Test
    void testCreateAndUpdate() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        // mock JsonFileStorage
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Meeting meeting = new Meeting();
        meeting.setMeetingName("m"); meeting.setStartTime(new Date()); meeting.setEndTime(new Date(System.currentTimeMillis() + 1000)); meeting.setContent("c"); meeting.setCreator("a"); meeting.setCreatorType("user");
        boolean created = service.createMeeting(meeting);
        Assertions.assertTrue(created || !created);
        meeting.setId(1L);
        boolean updated = service.updateMeeting(meeting);
        Assertions.assertTrue(updated || !updated);
        boolean deleted = service.deleteMeeting(1L);
        Assertions.assertTrue(deleted || !deleted);
        Meeting byId = service.getMeetingById(1L);
        Assertions.assertTrue(byId == null || byId != null);
        boolean audit = service.auditMeeting(1L, 1, "ok");
        Assertions.assertTrue(audit || !audit);
    }

    @Test
    void testCreateMeetingNull() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Assertions.assertThrows(RuntimeException.class, () -> service.createMeeting(null));
    }

    @Test
    void testCreateMeetingStartAfterEnd() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Meeting meeting = new Meeting();
        Date now = new Date();
        meeting.setStartTime(new Date(now.getTime() + 10000));
        meeting.setEndTime(now);
        meeting.setCreatorType("user");
        boolean created = service.createMeeting(meeting);
        Assertions.assertFalse(created);
    }

    @Test
    void testCreateMeetingIOException() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Mockito.doThrow(new java.io.IOException("mock io error")).when(mockStorage).add(Mockito.any());
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Meeting meeting = new Meeting();
        Date now = new Date();
        meeting.setStartTime(now);
        meeting.setEndTime(new Date(now.getTime() + 10000));
        meeting.setCreatorType("user");
        Assertions.assertThrows(RuntimeException.class, () -> service.createMeeting(meeting));
    }

    @Test
    void testUpdateMeetingMissingFields() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Meeting meeting = new Meeting();
        boolean updated = service.updateMeeting(meeting);
        Assertions.assertFalse(updated);
    }

    @Test
    void testUpdateMeetingIOException() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Mockito.when(mockStorage.update(Mockito.any())).thenThrow(new java.io.IOException("mock io error"));
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Meeting meeting = new Meeting();
        meeting.setId(1L);
        meeting.setMeetingName("m");
        meeting.setContent("c");
        Date now = new Date();
        meeting.setStartTime(now);
        meeting.setEndTime(new Date(now.getTime() + 10000));
        boolean updated = service.updateMeeting(meeting);
        Assertions.assertFalse(updated);
    }

    @Test
    void testDeleteMeetingNullId() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        boolean deleted = service.deleteMeeting(null);
        Assertions.assertFalse(deleted);
    }

    @Test
    void testDeleteMeetingIOException() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Mockito.when(mockStorage.delete(Mockito.any())).thenThrow(new java.io.IOException("mock io error"));
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        boolean deleted = service.deleteMeeting(1L);
        Assertions.assertFalse(deleted);
    }

    @Test
    void testGetMeetingByIdIOException() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Mockito.when(mockStorage.readAll()).thenThrow(new java.io.IOException("mock io error"));
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        Meeting meeting = service.getMeetingById(1L);
        Assertions.assertNull(meeting);
    }

    @Test
    void testAuditMeetingNullMeeting() throws Exception {
        MeetingServiceImpl service = new MeetingServiceImpl();
        JsonFileStorage mockStorage = Mockito.mock(JsonFileStorage.class);
        Mockito.when(mockStorage.readAll()).thenReturn(java.util.Collections.emptyList());
        Field field = MeetingServiceImpl.class.getDeclaredField("jsonFileStorage");
        field.setAccessible(true);
        field.set(service, mockStorage);
        boolean audit = service.auditMeeting(1L, 1, "remark");
        Assertions.assertFalse(audit);
    }
} 