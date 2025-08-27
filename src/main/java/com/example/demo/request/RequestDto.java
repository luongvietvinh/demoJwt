package com.example.demo.request;

import java.util.Set;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AddressDto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {


  /** 都道府県コード. */
  @NotNull(message = "not null")
  @NotEmpty(message = "not blank")
  private String userName;

  /** 市区町村CD. */
  @NotNull(message = "not null")
  @NotEmpty(message = "not blank")
  private String passWord;
  /** 市区町村CD. */
  @Email(message = "email fomat")
  private String mail;
  
  private Set<String> roles;


}
