package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.dto.ContructionDto;

@Mapper
public interface ContructionRepository {

  int createContruction(ContructionDto dto);
  void insertListContruction(List<ContructionDto> contructions);

  List<ContructionDto> getLisstContruction(@Param("limit") int limit, @Param("offset") int offset);

  ContructionDto getContructionById(String contructionId);

  void deleteContruction(String contructionId);

  void updateContruction(ContructionDto dto);
  
  boolean existsByContructionId(String contructionId);
}
