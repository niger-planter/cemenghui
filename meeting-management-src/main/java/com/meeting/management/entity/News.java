package com.meeting.management.entity;

import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class News {
    
    private Long id;

    private String title;

    private String author;

    private String category; // tech, trend, policy, company

    private String summary;

    private String content;

    private String coverImage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date publishDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer status; // 0: 草稿, 1: 已发布, 2: 已下架

    private Integer viewCount; // 浏览次数
} 