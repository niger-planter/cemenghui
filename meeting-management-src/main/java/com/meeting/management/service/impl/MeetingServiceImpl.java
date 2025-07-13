package com.meeting.management.service.impl;

import com.meeting.management.entity.Meeting;
import com.meeting.management.service.MeetingService;
import com.meeting.management.util.JsonFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private JsonFileStorage<Meeting> jsonFileStorage;



    @Override
    public List<Meeting> searchMeetings(Map<String, Object> params, int pageNum, int pageSize) {
        try {
            List<Meeting> allMeetings = jsonFileStorage.readAll();
            List<Meeting> filteredMeetings = new ArrayList<>();

            // 应用过滤条件
            for (Meeting meeting : allMeetings) {
                boolean match = true;

                if (params.containsKey("meetingName") && params.get("meetingName") != null && !params.get("meetingName").toString().isEmpty()) {
                    String meetingName = params.get("meetingName").toString();
                    if (!meeting.getMeetingName().contains(meetingName)) {
                        match = false;
                    }
                }

                if (params.containsKey("creator") && params.get("creator") != null && !params.get("creator").toString().isEmpty()) {
                    String creator = params.get("creator").toString();
                    if (!creator.equals(meeting.getCreator())) {
                        match = false;
                    }
                }

                if (params.containsKey("startTime") && params.get("startTime") != null) {
                    Date startTime = (Date) params.get("startTime");
                    if (meeting.getStartTime().before(startTime)) {
                        match = false;
                    }
                }

                // 只显示已审核通过的会议（如有需求可根据用户类型调整）
                if (meeting.getAuditStatus() == null || meeting.getAuditStatus() != 1) {
                    match = false;
                }

                if (match) {
                    filteredMeetings.add(meeting);
                }
            }

            // 按开始时间降序排序
            filteredMeetings.sort((m1, m2) -> m2.getStartTime().compareTo(m1.getStartTime()));

            // 分页处理
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, filteredMeetings.size());
            if (startIndex >= filteredMeetings.size()) {
                return new ArrayList<>();
            }

            return filteredMeetings.subList(startIndex, endIndex);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(MeetingServiceImpl.class);

    @Override
    public boolean createMeeting(Meeting meeting) {
        logger.info("开始创建会议: {}", meeting);
        // 验证关键字段
        if (meeting == null) {
            logger.error("创建会议失败: 会议对象为null");
throw new RuntimeException("会议创建失败: 会议对象为null");
        }
        if (meeting.getStartTime() == null) {
            logger.error("创建会议失败: 开始时间不能为空");
            return false;
        }
        if (meeting.getEndTime() == null) {
            logger.error("创建会议失败: 结束时间不能为空");
            return false;
        }
        if (meeting.getStartTime().after(meeting.getEndTime())) {
            logger.error("创建会议失败: 开始时间不能晚于结束时间");
            return false;
        }

        // 设置会议状态
        Date now = new Date();
        if (meeting.getEndTime().before(now)) {
            meeting.setStatus(1); // 已结束
        } else {
            meeting.setStatus(0); // 进行中
        }

        // 设置审核状态
        if ("enterprise".equals(meeting.getCreatorType())) {
            meeting.setAuditStatus(0); // 企业用户需审核
        } else {
            meeting.setAuditStatus(1); // 普通用户直接通过
        }

        meeting.setCreateTime(now);
        meeting.setUpdateTime(now);

        try {
            logger.info("保存前会议状态: {}", meeting);
            logger.info("开始执行存储操作");
            jsonFileStorage.add(meeting);
            logger.info("存储操作完成，更新后会议状态: {}", meeting);
            logger.info("会议创建成功: {}", meeting);
            logger.info("会议保存成功: ID={}", meeting.getId());
            return true;
        } catch (IOException e) {
            logger.error("会议保存失败: 文件操作异常", e);
            throw new RuntimeException("会议保存失败: 服务层处理异常", e);
        } catch (Exception e) {
            logger.error("会议保存失败: 未知异常", e);
            return false;
        }
    }

    @Override
    public boolean updateMeeting(Meeting meeting) {
        // 验证必填字段
        if (meeting.getId() == null ||
            meeting.getMeetingName() == null || meeting.getMeetingName().isEmpty() ||
            meeting.getContent() == null || meeting.getContent().isEmpty() ||
            meeting.getStartTime() == null || meeting.getEndTime() == null) {
            return false;
        }

        // 更新会议状态
        Date now = new Date();
        if (meeting.getEndTime().before(now)) {
            meeting.setStatus(1); // 已结束
        } else {
            meeting.setStatus(0); // 进行中
        }

        meeting.setUpdateTime(now);

        try {
            return jsonFileStorage.update(meeting);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteMeeting(Long id) {
        if (id == null) {
            return false;
        }
        try {
            return jsonFileStorage.delete(id);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Meeting getMeetingById(Long id) {
        try {
            List<Meeting> allMeetings = jsonFileStorage.readAll();
            for (Meeting meeting : allMeetings) {
                if (meeting.getId().equals(id)) {
                    return meeting;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean auditMeeting(Long id, Integer auditStatus, String auditRemark) {
        Meeting meeting = getMeetingById(id);
        if (meeting == null) return false;
        meeting.setAuditStatus(auditStatus);
        meeting.setAuditRemark(auditRemark);
        meeting.setUpdateTime(new Date());
        try {
            return jsonFileStorage.update(meeting);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}