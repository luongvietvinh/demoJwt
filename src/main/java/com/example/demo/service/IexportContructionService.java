package com.example.demo.service;

import java.io.IOException;
import jakarta.servlet.http.HttpServletResponse;

public interface IexportContructionService {
  
  void exportExcel(HttpServletResponse response) throws IOException;
  
  void exportCsv(HttpServletResponse response) throws IOException;

}
