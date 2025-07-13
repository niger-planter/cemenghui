package com.meeting.management.config;

import com.meeting.management.entity.Meeting;
import com.meeting.management.util.JsonFileStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.File;
import com.meeting.management.entity.Course;

@Configuration
public class AppConfig {

    @Bean
    public JsonFileStorage<Meeting> meetingJsonFileStorage() {
        String filePath = "c:\\Users\\22670\\实训\\meeting-management\\data\\meetings.json";       return new JsonFileStorage<>(filePath, Meeting.class);
    }

    @Bean
    public JsonFileStorage<Course> courseJsonFileStorage() {
        String filePath = "c:\\Users\\22670\\实训\\meeting-management\\data\\courses.json";
        return new JsonFileStorage<>(filePath, Course.class);
    }
}