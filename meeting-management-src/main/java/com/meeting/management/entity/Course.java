package com.meeting.management.entity;

import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class Course {
    private Long id;
    private String courseName;      // 课程名称
    private String coverImage;      // 课程封面
    private String description;     // 课程简介
    private Integer orderNum;       // 课程排序
    private String videoUrl;        // 课程视频地址
    private String author;          // 作者
    private Integer status;         // 0:未发布, 1:已发布
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Integer auditStatus;    // 0:待审核, 1:已通过, 2:未通过
    private String auditRemark;     // 审核备注
} 