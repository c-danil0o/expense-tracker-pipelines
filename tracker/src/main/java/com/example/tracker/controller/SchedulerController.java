package com.example.tracker.controller;

import com.example.tracker.dto.UserDTO;
import com.example.tracker.utils.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class SchedulerController {

    private final NotificationScheduler notificationScheduler;

    @GetMapping(value = "/notify")
    public ResponseEntity<Boolean> notifyUsers() {
        this.notificationScheduler.checkDailyForScheduledNotifications();
        return ResponseEntity.ok(true);
    }

}
