package com.example.demo.dto.request;

import java.sql.Timestamp;
import org.hibernate.validator.cfg.context.ReturnValueConstraintMappingContext;
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
  private String updateTime;

}
