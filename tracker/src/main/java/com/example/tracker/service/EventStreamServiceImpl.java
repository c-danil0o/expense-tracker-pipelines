package com.example.tracker.service;

import com.eclipsesource.json.JsonObject;
import com.example.tracker.model.User;
import com.example.tracker.service.interfaces.EventStreamService;
import com.example.tracker.utils.UserClientContext;
import com.example.tracker.utils.kafka.AvroSchemaGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class EventStreamServiceImpl implements EventStreamService {
    private final AvroSchemaGenerator avroSchemaGenerator;
    private KafkaTemplate<String, GenericRecord> kafkaTemplate;


    private GenericRecord generateAvroRecord(LocalDateTime timestamp, String type, String topic, String payload) throws JsonProcessingException {
        String messageSchema = avroSchemaGenerator.getSchema();
        Schema schema = new Schema.Parser().parse(messageSchema);
        GenericRecord record = new GenericData.Record(schema);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String user = null;
        if (principal instanceof UserDetails) {
            user = ((UserDetails)principal).getUsername();
        } else {
            user = principal.toString();
        }
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Map<String, String> clientInfo = UserClientContext.getCurrentContext();
        ObjectMapper objectMapper = new ObjectMapper();


        record.put("content", payload);
        record.put("client_info", objectMapper.writeValueAsString(clientInfo));
        record.put("timestamp", timestamp.toString());
        record.put("type", type);
        record.put("user_id", user);
        record.put("session_id", sessionId);
        record.put("topic", topic);
        return record;
    }

    @Override
    public void sendRecord(LocalDateTime timestamp, String type, String topic, String payload){
        GenericRecord record = null;
        try {
            record = generateAvroRecord(timestamp, type, topic, payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ProducerRecord<String, GenericRecord> kafkaRecord = new ProducerRecord<>(topic, null, record);
        this.kafkaTemplate.send(kafkaRecord);

    }




}
