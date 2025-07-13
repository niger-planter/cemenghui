package com.meeting.management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home() {
        return "Welcome to Meeting Management API. Available endpoints:\n" +
               "/api/meetings - GET: List meetings, POST: Create meeting\n" +
               "/api/meetings/{id} - PUT: Update meeting, DELETE: Delete meeting\n" +
               "/api/meetings/upload-cover - POST: Upload cover image\n" +
               "/api/meetings/export - GET: Export meetings to Excel";
    }
}