package com.example.demo.entity;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.demo.Role;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.utils.CustomTimestampDeserializer;
import com.example.demo.utils.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    private String userId;
    private String userName;
    private String passWord;
    private String mail;
    private Set<String> roles;
    private Boolean isEnabled;
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Timestamp updateAt;

    @JsonIgnore
    public Set<Role> getEnumRoles() {
      return roles.stream()
          .map(Role::fromValue)
          .collect(Collectors.toSet());
  }

  public static Users convertRequestToEntity(UpdateUserRequest request) {
    Users entity = Users.builder()
        .userId(request.getUserId())
        .userName(request.getUserName())
        .passWord(request.getPassWord())
        .roles(request.getRoles())
        .updateAt(DateTimeUtils.toTimestamp(request.getUpdateAt()))
        .build();
    return entity;
  }

}
