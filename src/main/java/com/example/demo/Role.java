package com.example.demo;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Role implements PersistentEnum {
  USER("1","user thuong"),
  ADMIN("2","user admin"), 
  SUPPORT("3","user support");
  
  private final String value;
  private final String displayName;

  @Override
  public String getValue() {
    // TODO Auto-generated method stub
    return value;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return name();
  }

  @Override
  public String getDisplayName() {
    // TODO Auto-generated method stub
    return displayName;
  }

  @Override
  public Map<String, String> getAll() {
    // TODO Auto-generated method stub
      return Arrays.stream(values())
        .collect(Collectors.toMap(Role::getValue, Role::getDisplayName));
  }
  
  // ✅ Optional: tìm enum từ value (dùng khi query DB)
  public static Role fromValue(String value) {
      return Arrays.stream(values())
              .filter(r -> r.value.equals(value))
              .findFirst()
              .orElseThrow(() -> new IllegalArgumentException("Invalid role value: " + value));
  }
  
}
