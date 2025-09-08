package com.example.demo.utils;

import java.io.IOException;
import java.sql.Timestamp;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class CustomTimestampSerializer extends StdSerializer<Timestamp> {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public CustomTimestampSerializer() {
    super(Timestamp.class);
}

@Override
public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    if (value != null) {
        gen.writeString(DateTimeUtils.formatTimestamp(value));
    } else {
        gen.writeNull();
    }
}

}
