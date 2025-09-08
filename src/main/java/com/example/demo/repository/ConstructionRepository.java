package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.dto.ConstructionDto;

@Mapper
public interface ConstructionRepository {

  int createConstruction(ConstructionDto dto);
  void insertListConstruction(List<ConstructionDto> constructions);

  List<ConstructionDto> getListConstruction(@Param("limit") int limit, @Param("offset") int offset);

  ConstructionDto getConstructionById(String constructionId);

  void deleteConstruction(String constructionId);

  int updateConstruction(ConstructionDto dto);
  
  boolean existsByConstructionId(String constructionId);
  
  int deleteOlderThan(@Param("threshold") LocalDateTime threshold);
}
