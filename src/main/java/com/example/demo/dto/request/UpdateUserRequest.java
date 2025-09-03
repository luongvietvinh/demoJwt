package com.example.demo.dto.request;

import com.example.demo.request.RequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AddressDto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest extends RequestDto {

  private String userId;
  private String updateAt;

}
