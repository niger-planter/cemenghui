package com.meeting.management.controller;

import com.meeting.management.entity.Meeting;
import com.meeting.management.service.MeetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
// 由于 SimpleDateFormat 被使用，此导入语句不应移除，故保留原导入语句
// 由于在代码中多处使用了 SimpleDateFormat，此导入语句实际被使用，不应移除，故保留原导入语句
import java.text.SimpleDateFormat;
import java.util.List;
// 由于 java.util.Date 未被使用，移除该导入语句

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    private static final Logger log = LoggerFactory.getLogger(MeetingController.class);

    @Autowired
    private MeetingService meetingService;

    /**
     * 搜索会议
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchMeetings(
            @RequestParam(required = false) String meetingName,
            @RequestParam(required = false) String creator,
            @RequestParam(required = false) String startTime,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {

        Map<String, Object> params = new HashMap<>();
        params.put("meetingName", meetingName);
        params.put("creator", creator);
        if (startTime != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
java.util.Date parsedStartTime = sdf.parse(startTime);
                params.put("startTime", parsedStartTime);
            } catch (ParseException e) {
                e.printStackTrace();
                params.put("startTime", null);
            }
        } else {
            params.put("startTime", null);
        }

        // 获取过滤后的所有会议用于计算总数
        List<Meeting> allFilteredMeetings = meetingService.searchMeetings(params, 1, Integer.MAX_VALUE);
        // 获取当前页数据
        List<Meeting> meetingList = meetingService.searchMeetings(params, pageNum, pageSize);
        
        Map<String, Object> result = new HashMap<>();
// 由于 meeting 变量未定义，这里推测可能是想记录搜索参数，改为记录 params 变量
log.info("搜索会议请求参数: {}", params);
        result.put("records", meetingList);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("total", allFilteredMeetings.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 创建会议
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMeeting(
            @RequestBody Meeting meeting) {

        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = meetingService.createMeeting(meeting);
            if (success) {
                result.put("success", true);
                result.put("message", "会议创建成功");
                result.put("data", meeting);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "会议创建失败: 服务处理返回失败");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "会议创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 更新会议
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMeeting(
            @PathVariable Long id,
            @RequestBody Meeting meeting) {

        meeting.setId(id);
        Map<String, Object> result = new HashMap<>();
        boolean success = meetingService.updateMeeting(meeting);

        if (success) {
            result.put("success", true);
            result.put("message", "会议更新成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "会议更新失败，必填字段不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 删除会议
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMeeting(
            @PathVariable Long id) {

        Map<String, Object> result = new HashMap<>();
        boolean success = meetingService.deleteMeeting(id);

        if (success) {
            result.put("success", true);
            result.put("message", "会议删除成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "会议删除失败");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    /**
     * 上传会议封面图片
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

    /**
     * 导出会议Excel
     */
    @GetMapping("/export")
    public void exportMeetings(HttpServletResponse response) throws IOException {
        // 获取所有会议数据（不分页）
        Map<String, Object> params = new HashMap<>();
        List<Meeting> meetings = meetingService.searchMeetings(params, 1, Integer.MAX_VALUE);

        // 创建Excel工作簿
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("会议列表");

        // 创建表头
        String[] headers = {"会议名称", "创建人", "开始时间", "结束时间", "状态"};
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (int i = 0; i < meetings.size(); i++) {
            Meeting meeting = meetings.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(meeting.getMeetingName());
            row.createCell(1).setCellValue(meeting.getCreator());
            row.createCell(2).setCellValue(sdf.format(meeting.getStartTime()));
            row.createCell(3).setCellValue(sdf.format(meeting.getEndTime()));
            row.createCell(4).setCellValue(meeting.getStatus() == 0 ? "进行中" : "已结束");
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=meetings_" + System.currentTimeMillis() + ".xlsx");

        // 写入响应流
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }

    /**
     * 获取会议详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMeetingDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Meeting meeting = meetingService.getMeetingById(id);
        if (meeting != null) {
            result.put("success", true);
            result.put("data", meeting);
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "会议不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * 审核会议（仅管理员）
     */
    @PutMapping("/{id}/audit")
    public ResponseEntity<Map<String, Object>> auditMeeting(@PathVariable Long id, @RequestBody Map<String, Object> auditInfo) {
        Integer auditStatus = (Integer) auditInfo.get("auditStatus");
        String auditRemark = (String) auditInfo.get("auditRemark");
        Map<String, Object> result = new HashMap<>();
        boolean success = meetingService.auditMeeting(id, auditStatus, auditRemark);
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
}