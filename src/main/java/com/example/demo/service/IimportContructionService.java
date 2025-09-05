package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

public interface IimportContructionService {
  
  void importCsv(MultipartFile file) throws Exception;
  
  void importExcel(MultipartFile file) throws Exception;
  
}
