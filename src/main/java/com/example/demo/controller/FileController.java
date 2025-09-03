package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.impl.S3FileService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final S3FileService s3FileService;

//    @GetMapping("/download")
//    public ResponseEntity<String> downloadFile(@RequestParam String s3Key) {
//        String url = s3FileService.generatePresignedUrl(s3Key, 10); // hết hạn sau 10 phút
//        return ResponseEntity.ok(url);
//    }
}
