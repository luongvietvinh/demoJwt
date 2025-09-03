package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import com.example.demo.dto.ContructionDto;

public interface IcontructionService {

  ContructionDto createContruction(ContructionDto dto);
  Optional<ContructionDto> getContructionById(String contructionId);
  List<ContructionDto> getListContruction(int page, int size);
  void deleteContruction(String contructionId);
  ContructionDto updateContruction(ContructionDto dto) throws Exception;
}
