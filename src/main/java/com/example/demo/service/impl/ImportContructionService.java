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
import com.example.demo.dto.ConstructionDto;
import com.example.demo.repository.ConstructionRepository;
import com.example.demo.service.IimportContructionService;
import com.example.demo.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportContructionService implements IimportContructionService {

  private final ConstructionRepository contructionRepo;
  
  @Override
  public void importCsv(MultipartFile file) throws IOException {

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String line;
      boolean skipHeader = true;
      List<ConstructionDto> list = new ArrayList<>();

      while ((line = reader.readLine()) != null) {
          if (skipHeader) { skipHeader = false; continue; }
          String[] parts = line.split(",");
          ConstructionDto c = new ConstructionDto();
          c.setConstructionId(line);
          c.setZipCode(line);
          c.setAddress(parts[2]);
          c.setCreatedAt(DateTimeUtils.toTimestamp(parts[3]));
          list.add(c);
      }

      if (!list.isEmpty()) {
        contructionRepo.insertListConstruction(list);
      }
  }
  }

  @Override
  public void importExcel(MultipartFile file) throws IOException {
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);
      Iterator<Row> rows = sheet.iterator();
      List<ConstructionDto> list = new ArrayList<>();

      boolean skipHeader = true;
      while (rows.hasNext()) {
          Row row = rows.next();
          if (skipHeader) { skipHeader = false; continue; }

          ConstructionDto c = new ConstructionDto();
          c.setConstructionId(row.getCell(0).getStringCellValue());
          c.setConstructionName(row.getCell(1).getStringCellValue());
          c.setAddress(row.getCell(2).getStringCellValue());
          c.setCreatedAt(DateTimeUtils.toTimestamp(row.getCell(3).getStringCellValue()));
          list.add(c);
      }

      if (!list.isEmpty()) {
        contructionRepo.insertListConstruction(list);
      }
    }

  }

}
