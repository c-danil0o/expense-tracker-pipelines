package com.expense.consumer.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String content;
    private String client_info;
    private String timestamp;
    private String type;
    private String user_id;
    private String session_id;
    private String topic;
}
