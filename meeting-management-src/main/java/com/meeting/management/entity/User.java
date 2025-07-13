package com.meeting.management.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String role; // user/admin
    private Boolean enabled = true;
} 