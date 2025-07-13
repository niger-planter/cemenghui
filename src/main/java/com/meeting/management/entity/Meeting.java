package com.meeting.management.entity;

import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data

public class Meeting {
    
    private Long id;

    
    private String meetingName;

    
    private String creator;

    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    
    private Integer status; // 0: 进行中, 1: 已结束

    
    private String content;

    
    private String coverImage;

    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    
    private Integer auditStatus; // 0:待审核, 1:已通过, 2:未通过
    private String auditRemark; // 审核备注
    private String creatorType; // user/enterprise/admin
}