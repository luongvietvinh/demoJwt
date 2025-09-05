package com.example.demo.service;

import jakarta.servlet.http.HttpServletResponse;

public interface IexportContructionService {
  
  void exportExcel(HttpServletResponse response) throws Exception;
  
  void exportCsv(HttpServletResponse response) throws Exception;

}
