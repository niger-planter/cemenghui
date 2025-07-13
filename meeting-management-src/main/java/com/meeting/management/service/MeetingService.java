package com.meeting.management.service;

import com.meeting.management.entity.Meeting;
import java.util.List;
import java.util.Map;

public interface MeetingService {
    boolean deleteMeeting(Long id);
    boolean createMeeting(Meeting meeting);
    boolean updateMeeting(Meeting meeting);
    List<Meeting> searchMeetings(Map<String, Object> params, int pageNum, int pageSize);
    Meeting getMeetingById(Long id);
    boolean auditMeeting(Long id, Integer auditStatus, String auditRemark);
}