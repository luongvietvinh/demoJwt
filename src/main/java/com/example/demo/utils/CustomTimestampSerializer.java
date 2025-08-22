package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Custom Timestamp Serializer with format "yyyy-MM-dd HH:mm:ss".
 * Bộ serializer Timestamp tùy chỉnh với định dạng "yyyy-MM-dd HH:mm:ss".
 */
public class CustomTimestampSerializer extends JsonSerializer<Timestamp> {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value != null ? formatter.format(value) : null);
    }
}
