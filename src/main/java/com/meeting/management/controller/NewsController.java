package com.meeting.management.controller;

import com.meeting.management.entity.News;
import com.meeting.management.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    private static final Logger log = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    /**
     * 搜索新闻
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchNews(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String publishDate,
            @RequestParam(required = false) String publishDateStart,
            @RequestParam(required = false) String publishDateEnd,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "12") int pageSize) {

        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("author", author);
        params.put("category", category);
        if (publishDateStart != null && publishDateEnd != null) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date start = sdf.parse(publishDateStart.substring(0, 10));
                java.util.Date end = sdf.parse(publishDateEnd.substring(0, 10));
                params.put("publishDateStart", start);
                params.put("publishDateEnd", end);
            } catch (java.text.ParseException e) {
                params.put("publishDateStart", null);
                params.put("publishDateEnd", null);
            }
        } else if (publishDate != null) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                java.util.Date parsedPublishDate = sdf.parse(publishDate);
                params.put("publishDate", parsedPublishDate);
            } catch (java.text.ParseException e) {
                params.put("publishDate", null);
            }
        } else {
            params.put("publishDate", null);
        }

        // 获取过滤后的所有新闻用于计算总数
        List<News> allFilteredNews = newsService.searchNews(params, 1, Integer.MAX_VALUE);
        // 获取当前页数据
        List<News> newsList = newsService.searchNews(params, pageNum, pageSize);
        
        Map<String, Object> result = new HashMap<>();
        log.info("搜索新闻请求参数: {}", params);
        result.put("success", true);
        result.put("records", newsList);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("total", allFilteredNews.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 创建新闻
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNews(@RequestBody News news) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = newsService.createNews(news);
            if (success) {
                result.put("success", true);
                result.put("message", "新闻发布成功");
                result.put("data", news);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "新闻发布失败: 必填字段不能为空");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "新闻发布失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 更新新闻
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNews(
            @PathVariable Long id,
            @RequestBody News news) {

        news.setId(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = newsService.updateNews(news);

        if (success) {
            result.put("success", true);
            result.put("message", "新闻更新成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "新闻更新失败，必填字段不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 删除新闻
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNews(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = newsService.deleteNews(id);

        if (success) {
            result.put("success", true);
            result.put("message", "新闻删除成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "新闻删除失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 获取新闻详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNewsById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        News news = newsService.getNewsById(id);

        if (news != null) {
            // 增加浏览次数
            newsService.incrementViewCount(id);
            result.put("success", true);
            result.put("data", news);
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "新闻不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * 上传新闻封面图片
     */
    @PostMapping("/upload-cover")
    public ResponseEntity<Map<String, Object>> uploadCoverImage(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "上传文件不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.matches("image/(png|jpg|jpeg)")) {
            result.put("success", false);
            result.put("message", "只支持png、jpg、jpeg格式的图片");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + suffix;

        // 上传文件路径
        String uploadDir = System.getProperty("user.dir") + "/uploads/covers/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            // 保存文件
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // 返回文件访问路径
            String fileUrl = "/uploads/covers/" + fileName;
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
} 