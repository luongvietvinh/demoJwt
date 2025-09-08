package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.dto.ConstructionDto;
import com.example.demo.dto.FileDiffResult;
import com.example.demo.dto.FileDto;
import com.example.demo.repository.FileRepository;

@Service
public class FileSyncCommonService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private S3FileService s3FileService;

    public FileDiffResult syncFiles(ConstructionDto dto) {
        List<FileDto> uploadedNow = new ArrayList<>();

        // 1. DB snapshot
        List<FileDto> dbFiles = fileRepository.getFilesByConstructionId(dto.getConstructionId());
        Set<String> dbUuids = dbFiles.stream().map(FileDto::getUuId).collect(Collectors.toSet());

        // 2. Files từ client (metadata)
        List<FileDto> clientFilesMeta = Optional.ofNullable(dto.getFiles()).orElse(Collections.emptyList());
        Set<String> clientUuids = clientFilesMeta.stream().map(FileDto::getUuId).collect(Collectors.toSet());

        // 3. Upload file mới từ FE
        if (dto.getUploadFiles() != null && !dto.getUploadFiles().isEmpty()) {
            uploadedNow = s3FileService.uploadFiles(dto.getUploadFiles(), dto.getUserID(), dto.getUserName(), dto.getConstructionId());
        }
        Set<String> uploadedUuids = uploadedNow.stream().map(FileDto::getUuId).collect(Collectors.toSet());

        // 4. Files FE cuối cùng muốn giữ
        Set<String> finalIntended = new HashSet<>(clientUuids);
        finalIntended.addAll(uploadedUuids);

        // 5. toInsert
        List<FileDto> toInsert = new ArrayList<>();
        for (FileDto f : uploadedNow) {
            if (!dbUuids.contains(f.getUuId())) toInsert.add(f);
        }
        for (FileDto cf : clientFilesMeta) {
            if (!dbUuids.contains(cf.getUuId())) toInsert.add(cf);
        }

        // 6. toDelete
        List<FileDto> toDelete = dbFiles.stream()
                .filter(dbf -> !finalIntended.contains(dbf.getUuId()))
                .collect(Collectors.toList());

        return new FileDiffResult(uploadedNow, toInsert, toDelete);
    }
}
