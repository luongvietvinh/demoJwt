package com.example.demo.dto.request;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.FileDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContructionReigisterRequest {
    
    private String userID;
    private String userName;
    private String contructionId;
    
    @NotBlank(message = "Construction name không được để trống")
    private String contructionName;
    
    @NotBlank(message = "Address không được để trống")
    private String address;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number phải là 10-11 số")
    private String phoneNumber;
    
    private String zipCode;
    
    @Email(message = "Email không đúng định dạng")
    private String mail;
    
    // Files từ form upload
    private List<MultipartFile> uploadFiles;
    
    // Files đã xử lý (cho DTO)
    private List<FileDto> files;
}