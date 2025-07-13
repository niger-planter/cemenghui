package com.meeting.management.service.impl;

import com.meeting.management.entity.Course;
import com.meeting.management.service.CourseService;
import com.meeting.management.util.JsonFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.io.IOException;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private JsonFileStorage<Course> courseJsonFileStorage;

    @Override
    public List<Course> searchCourses(Map<String, Object> params, int pageNum, int pageSize) {
        try {
            List<Course> allCourses = courseJsonFileStorage.readAll();
            List<Course> filteredCourses = new ArrayList<>();
            for (Course course : allCourses) {
                boolean match = true;
                if (params.containsKey("courseName") && params.get("courseName") != null && !params.get("courseName").toString().isEmpty()) {
                    String courseName = params.get("courseName").toString();
                    if (!course.getCourseName().contains(courseName)) {
                        match = false;
                    }
                }
                if (params.containsKey("author") && params.get("author") != null && !params.get("author").toString().isEmpty()) {
                    String author = params.get("author").toString();
                    if (!course.getAuthor().contains(author)) {
                        match = false;
                    }
                }
                // 不再限制只显示已审核通过的课程
                if (match) {
                    filteredCourses.add(course);
                }
            }
            filteredCourses.sort((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime()));
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, filteredCourses.size());
            if (startIndex >= filteredCourses.size()) {
                return new ArrayList<>();
            }
            return filteredCourses.subList(startIndex, endIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean createCourse(Course course) {
        if (course == null) return false;
        Date now = new Date();
        course.setCreateTime(now);
        course.setUpdateTime(now);
        course.setAuditStatus(0); // 默认待审核
        if (course.getId() == null) {
            course.setId(System.currentTimeMillis());
        }
        try {
            courseJsonFileStorage.add(course);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateCourse(Course course) {
        if (course == null || course.getId() == null) return false;
        course.setUpdateTime(new Date());
        try {
            return courseJsonFileStorage.update(course);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteCourse(Long id) {
        if (id == null) return false;
        try {
            return courseJsonFileStorage.delete(id);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Course getCourseById(Long id) {
        try {
            List<Course> allCourses = courseJsonFileStorage.readAll();
            for (Course course : allCourses) {
                if (course.getId().equals(id)) {
                    return course;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean auditCourse(Long id, Integer auditStatus, String auditRemark) {
        Course course = getCourseById(id);
        if (course == null) return false;
        course.setAuditStatus(auditStatus);
        course.setAuditRemark(auditRemark);
        course.setUpdateTime(new Date());
        try {
            return courseJsonFileStorage.update(course);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
} 