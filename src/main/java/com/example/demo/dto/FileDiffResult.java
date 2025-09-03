package com.example.demo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDiffResult {
    private List<FileDto> uploadedNow; // file vừa upload
    private List<FileDto> toInsert;    // file cần insert DB
    private List<FileDto> toDelete;    // file cần xóa (DB + S3)
}