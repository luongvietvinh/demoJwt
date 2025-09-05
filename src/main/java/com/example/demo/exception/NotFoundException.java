package com.example.demo.exception;

import com.example.demo.utils.MessageUtils;

public class NotFoundException extends RuntimeException {
  
  public NotFoundException(String message) {
    super(message);
}

}
