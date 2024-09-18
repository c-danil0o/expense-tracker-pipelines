package com.example.tracker.service.interfaces;

import java.time.LocalDateTime;

public interface EventStreamService {
    void sendRecord(LocalDateTime timestamp, String type, String topic);
}
