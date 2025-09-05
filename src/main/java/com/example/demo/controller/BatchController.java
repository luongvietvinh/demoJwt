package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.ContructionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchController {

    private final ContructionService contructionService;


    @DeleteMapping("/cleanup")
    public ResponseEntity<String> cleanup() {
      contructionService.deleteOldData();
        return ResponseEntity.ok("Cleanup done");
    }
}
