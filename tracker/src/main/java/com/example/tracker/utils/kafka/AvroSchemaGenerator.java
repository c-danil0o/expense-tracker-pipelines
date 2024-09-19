package com.example.tracker.utils.kafka;

public class AvroSchemaGenerator {
    private final String schema;

    public AvroSchemaGenerator(String schema) {
        this.schema = schema;
    }

    public String getSchema(){
        return schema;
    }
}
