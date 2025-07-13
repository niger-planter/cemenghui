package com.meeting.management.service;

import com.meeting.management.entity.Course;
import java.util.List;
import java.util.Map;

public interface CourseService {
    boolean deleteCourse(Long id);
    boolean createCourse(Course course);
    boolean updateCourse(Course course);
    List<Course> searchCourses(Map<String, Object> params, int pageNum, int pageSize);
    Course getCourseById(Long id);
    boolean auditCourse(Long id, Integer auditStatus, String auditRemark);
} 