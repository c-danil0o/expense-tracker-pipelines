package com.example.tracker.service;

import com.example.tracker.model.User;
import com.example.tracker.service.interfaces.EventStreamService;
import com.example.tracker.utils.kafka.AvroSchemaGenerator;
import lombok.AllArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class EventStreamServiceImpl implements EventStreamService {
    private final AvroSchemaGenerator avroSchemaGenerator;
    private KafkaTemplate<String, GenericRecord> kafkaTemplate;


    private GenericRecord generateAvroRecord(LocalDateTime timestamp, String type, String topic){
        String messageSchema = avroSchemaGenerator.getSchema();
        Schema schema = new Schema.Parser().parse(messageSchema);
        GenericRecord record = new GenericData.Record(schema);

        String user = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        record.put("content", "123");
        record.put("timestamp", timestamp.toString());
        record.put("type", type);
        record.put("user_id", user);
        record.put("session_id", sessionId);
        record.put("topic", topic);
        return record;
    }

    @Override
    public void sendRecord(LocalDateTime timestamp, String type, String topic){
        GenericRecord record = generateAvroRecord(timestamp, type, topic);
        ProducerRecord<String, GenericRecord> kafkaRecord = new ProducerRecord<>(topic, null, record);
        this.kafkaTemplate.send(kafkaRecord);

    }




}
