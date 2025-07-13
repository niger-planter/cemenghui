package com.meeting.management;

import com.meeting.management.controller.MeetingController;
import com.meeting.management.entity.Meeting;
import com.meeting.management.service.MeetingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.*;

public class MeetingControllerTest {
    @Test
    void testCreateMeetingWithEmpty() throws Exception {
        MeetingController controller = new MeetingController();
        MeetingService mockService = Mockito.mock(MeetingService.class);
        Mockito.when(mockService.createMeeting(Mockito.any())).thenReturn(false);
        Field field = MeetingController.class.getDeclaredField("meetingService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Meeting meeting = new Meeting();
        ResponseEntity<Map<String, Object>> resp = controller.createMeeting(meeting);
        Assertions.assertNotNull(resp);
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }
    @Test
    void testCreateMeetingSuccess() throws Exception {
        MeetingController controller = new MeetingController();
        MeetingService mockService = Mockito.mock(MeetingService.class);
        Mockito.when(mockService.createMeeting(Mockito.any())).thenReturn(true);
        Field field = MeetingController.class.getDeclaredField("meetingService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Meeting meeting = new Meeting();
        meeting.setMeetingName("m");
        meeting.setCreator("a");
        ResponseEntity<Map<String, Object>> resp = controller.createMeeting(meeting);
        Assertions.assertTrue((Boolean)resp.getBody().get("success"));
    }
    @Test
    void testUpdateMeetingNotFound() throws Exception {
        MeetingController controller = new MeetingController();
        MeetingService mockService = Mockito.mock(MeetingService.class);
        Mockito.when(mockService.updateMeeting(Mockito.any())).thenReturn(false);
        Field field = MeetingController.class.getDeclaredField("meetingService");
        field.setAccessible(true);
        field.set(controller, mockService);
        Meeting meeting = new Meeting();
        meeting.setId(999L);
        ResponseEntity<Map<String, Object>> resp = controller.updateMeeting(999L, meeting);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }
    @Test
    void testDeleteMeetingNotFound() throws Exception {
        MeetingController controller = new MeetingController();
        MeetingService mockService = Mockito.mock(MeetingService.class);
        Mockito.when(mockService.deleteMeeting(Mockito.anyLong())).thenReturn(false);
        Field field = MeetingController.class.getDeclaredField("meetingService");
        field.setAccessible(true);
        field.set(controller, mockService);
        ResponseEntity<Map<String, Object>> resp = controller.deleteMeeting(999L);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }
    @Test
    void testUploadCoverImageEmpty() {
        MeetingController controller = new MeetingController();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }
    @Test
    void testUploadCoverImageFormatError() {
        MeetingController controller = new MeetingController();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        ResponseEntity<Map<String, Object>> resp = controller.uploadCoverImage(file);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        Assertions.assertFalse((Boolean)resp.getBody().get("success"));
    }
} 