package com.meeting.management.controller;

import com.meeting.management.entity.Course;
import com.meeting.management.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;
import java.io.IOException;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    /**
     * 搜索课程
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchCourses(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("courseName", courseName);
        params.put("author", author);
        List<Course> allFilteredCourses = courseService.searchCourses(params, 1, Integer.MAX_VALUE);
        List<Course> courseList = courseService.searchCourses(params, pageNum, pageSize);
        Map<String, Object> result = new HashMap<>();
        result.put("records", courseList);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("total", allFilteredCourses.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 创建课程
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        Map<String, Object> result = new HashMap<>();
        boolean success = courseService.createCourse(course);
        if (success) {
            result.put("success", true);
            result.put("message", "课程创建成功");
            result.put("data", course);
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "课程创建失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = courseService.updateCourse(course);
        if (success) {
            result.put("success", true);
            result.put("message", "课程更新成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "课程更新失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = courseService.deleteCourse(id);
        if (success) {
            result.put("success", true);
            result.put("message", "课程删除成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "课程删除失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Course course = courseService.getCourseById(id);
        if (course != null) {
            result.put("success", true);
            result.put("data", course);
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "课程不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * 审核课程
     */
    @PutMapping("/{id}/audit")
    public ResponseEntity<Map<String, Object>> auditCourse(@PathVariable Long id, @RequestBody Map<String, Object> auditInfo) {
        Integer auditStatus = (Integer) auditInfo.get("auditStatus");
        String auditRemark = (String) auditInfo.get("auditRemark");
        Map<String, Object> result = new HashMap<>();
        boolean success = courseService.auditCourse(id, auditStatus, auditRemark);
        if (success) {
            result.put("success", true);
            result.put("message", "审核操作成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "审核操作失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 上传课程封面图片
     */
    @PostMapping("/upload-cover")
    public ResponseEntity<Map<String, Object>> uploadCoverImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.matches("image/(png|jpg|jpeg)")) {
            result.put("success", false);
            result.put("message", "只支持png、jpg、jpeg格式的图片");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        String uploadDir = System.getProperty("user.dir") + "/uploads/covers/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            String fileUrl = "/uploads/covers/" + fileName;
            fileUrl = fileUrl.replace("\\", "/").replace("\\\\", "/");
            result.put("success", true);
            result.put("message", "图片上传成功");
            result.put("data", fileUrl);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "图片上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 上传课程视频
     */
    @PostMapping("/upload-video")
    public ResponseEntity<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.matches("video/(mp4|avi|mov)")) {
            result.put("success", false);
            result.put("message", "只支持mp4、avi、mov格式的视频");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;
        String uploadDir = System.getProperty("user.dir") + "/uploads/videos/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            String fileUrl = "/uploads/videos/" + fileName;
            fileUrl = fileUrl.replace("\\", "/").replace("\\\\", "/");
            result.put("success", true);
            result.put("message", "视频上传成功");
            result.put("data", fileUrl);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "视频上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
} 