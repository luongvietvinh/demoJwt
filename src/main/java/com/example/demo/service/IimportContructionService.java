package com.example.demo.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface IimportContructionService {
  
  void importCsv(MultipartFile file) throws IOException;
  
  void importExcel(MultipartFile file) throws IOException;
  
}
