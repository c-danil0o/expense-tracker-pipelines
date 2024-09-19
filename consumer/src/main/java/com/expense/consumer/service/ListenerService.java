package com.expense.consumer.service;

import com.expense.consumer.model.Event;
import com.expense.consumer.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.apache.avro.generic.GenericRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ListenerService {

    private final EventRepository eventRepository;

    @KafkaListener(topics = {"transaction", "user", "reminder", "savings"}, groupId = "cons", containerFactory = "kafkaRecordListenerContainerFactory")
    public void listenToEvents(GenericRecord record){
        String content = null;
        String client_info = null;
        if (record.get("content") != null)
            content = record.get("content").toString();

        if (record.get("client_info") != null)
            client_info = record.get("client_info").toString();

        Event event = Event.builder().
                type(record.get("type").toString()).
                content(content).
                user_id(record.get("user_id").toString()).
                session_id(record.get("session_id").toString()).
                client_info(client_info).
                timestamp(record.get("timestamp").toString()).
                topic(record.get("topic").toString()).
                feature_type(record.get("feature_type").toString()).
                build();
        this.eventRepository.save(event);
    }


}
