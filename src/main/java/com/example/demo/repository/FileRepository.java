package com.example.demo.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.dto.FileDto;

@Mapper
public interface FileRepository {
    
    int insertFile(FileDto file);
    
    int insertListFile(@Param("files") List<FileDto> files, 
                      @Param("constructionId") String constructionId);
    
    List<FileDto> getFilesByConstructionId(String constructionId);
    
    int deleteFile(String uuId);
    
    int deleteFilesByConstructionId(String constructionId);

    int deleteFilesByUuids(List<String> deleteUuids);
}