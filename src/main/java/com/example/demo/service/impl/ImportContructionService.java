package com.example.demo.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.dto.ContructionDto;
import com.example.demo.repository.ContructionRepository;
import com.example.demo.service.IimportContructionService;
import com.example.demo.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportContructionService implements IimportContructionService {

  private final ContructionRepository contructionRepo;
  
  @Override
  public void importCsv(MultipartFile file) throws IOException {

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String line;
      boolean skipHeader = true;
      List<ContructionDto> list = new ArrayList<>();

      while ((line = reader.readLine()) != null) {
          if (skipHeader) { skipHeader = false; continue; }
          String[] parts = line.split(",");
          ContructionDto c = new ContructionDto();
          c.setContructionId(line);
          c.setZipCode(line);
          c.setAddress(parts[2]);
          c.setCreatedAt(DateTimeUtils.toTimestamp(parts[3]));
          list.add(c);
      }

      if (!list.isEmpty()) {
        contructionRepo.insertListContruction(list);
      }
  }
  }

  @Override
  public void importExcel(MultipartFile file) throws IOException {
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      List<ContructionDto> list = new ArrayList<>();

      boolean skipHeader = true;
      while (rows.hasNext()) {
          Row row = rows.next();
          if (skipHeader) { skipHeader = false; continue; }

          ContructionDto c = new ContructionDto();
          c.setContructionId(row.getCell(0).getStringCellValue());
          c.setContructionName(row.getCell(1).getStringCellValue());
          c.setAddress(row.getCell(2).getStringCellValue());
          c.setCreatedAt(DateTimeUtils.toTimestamp(row.getCell(3).getStringCellValue()));
          list.add(c);
      }

      if (!list.isEmpty()) {
        contructionRepo.insertListContruction(list);
      }
    }

  }

}
