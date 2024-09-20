package com.expense.consumer.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity(name = "raw_event_bronze")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long event_id;

    private String payload;
    private String client_info;
    private String timestamp;
    private String type;
    private String user_email;
    private String session_id;
    private String topic;
    private String feature_type;
}
