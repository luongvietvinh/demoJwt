package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Custom String Deserializer to trim input values.
 * Bộ giải mã chuỗi tùy chỉnh để trim giá trị đầu vào.
 */
public class CustomStringDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return value != null ? value.trim() : null;
    }
}
