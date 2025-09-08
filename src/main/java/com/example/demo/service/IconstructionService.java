package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import com.example.demo.dto.ConstructionDto;

public interface IconstructionService {

  ConstructionDto createConstruction(ConstructionDto dto);
  Optional<ConstructionDto> getConstructionById(String constructionId);
  List<ConstructionDto> getListConstruction(int page, int size);
  void deleteConstruction(String constructionId);
  ConstructionDto updateConstruction(ConstructionDto dto) throws Exception;
}
