package com.expense.consumer.service;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ListenerService {
    @KafkaListener(topics = "transaction", groupId = "cons", containerFactory = "kafkaRecordListenerContainerFactory")
    public void listenTransactions(GenericRecord record){
        System.out.println("Content " + record.get("content"));
        System.out.println("Timestamp " + record.get("timestamp"));
        System.out.println("User " + record.get("user_id"));
        System.out.println("Session " + record.get("session_id"));
        System.out.println("Type " + record.get("type"));
        System.out.println("Topic " + record.get("topic"));
    }
}
