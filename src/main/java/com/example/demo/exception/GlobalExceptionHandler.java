package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;


@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
      List<String> errors = ex.getBindingResult()
                              .getFieldErrors()
                              .stream()
                              .map(err -> err.getField() + ": " + err.getDefaultMessage())
                              .collect(Collectors.toList());

      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
      errorResponse.put("error", "Validation Failed");
      errorResponse.put("message", errors);
      errorResponse.put("path", request.getRequestURI());

      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Object> handleValidationExceptions(ValidationException ex) {
      return new ResponseEntity<>(ex.getErrors(), HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Map<String, Object>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()); // 415
      errorResponse.put("error", "Unsupported Media Type");
      errorResponse.put("message", ex.getMessage());
      errorResponse.put("path", request.getRequestURI());
      return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }
  
//Bắt lỗi IllegalArgumentException
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
      errorResponse.put("error", "Bad Request");
      errorResponse.put("message", ex.getMessage());

      return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  // Bắt lỗi chung (fallback)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
      errorResponse.put("error", "Internal Server Error");
      errorResponse.put("message", ex.getMessage());

      return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
  // Bắt lỗi check quyền (fallback)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", HttpStatus.FORBIDDEN.value()); // 403
      errorResponse.put("error", "Forbidden");
      errorResponse.put("message", "Bạn không có quyền truy cập vào chức năng này");
      errorResponse.put("path", request.getRequestURI());

      return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }
}
