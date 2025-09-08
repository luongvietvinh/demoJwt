package com.example.demo.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.example.demo.dto.ConstructionDto;
import com.example.demo.repository.ConstructionRepository;
import com.example.demo.service.IexportContructionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExportContructionService implements IexportContructionService {
  
  private final ConstructionRepository contructionRepo;
  

  @Override
  public void exportExcel(HttpServletResponse response) throws IOException {
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=constructions.xlsx");

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Constructions");

    // Header
    Row headerRow = sheet.createRow(0);
    headerRow.createCell(0).setCellValue("Contruction ID");
    headerRow.createCell(1).setCellValue("Contruction Name");
    headerRow.createCell(2).setCellValue("Address");
    headerRow.createCell(3).setCellValue("Create time");

    int rowIdx = 1;
    int offset = 0;
    List<ConstructionDto> batch;
    do {
        batch = contructionRepo.getListConstruction(1000, offset);
        for (ConstructionDto c : batch) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(c.getConstructionId());
            row.createCell(1).setCellValue(c.getConstructionName());
            row.createCell(2).setCellValue(c.getAddress());
            row.createCell(3).setCellValue(c.getCreatedAt().toString());
        }
        offset += 1000;
    } while (!batch.isEmpty());

    workbook.write(response.getOutputStream());
    workbook.close();

  }

  @Override
  public void exportCsv(HttpServletResponse response) throws IOException {
    response.setContentType("text/csv; charset=UTF-8");
    response.setHeader("Content-Disposition", "attachment; filename=constructions.csv");

    PrintWriter writer = response.getWriter();
    writer.println("Contruction ID,Contruction Name,Address,Create time");

    int offset = 0;
    List<ConstructionDto> batch;
    do {
        batch = contructionRepo.getListConstruction(1000, offset);
        for (ConstructionDto c : batch) {
            writer.println(String.format("%s,%s,%s,%s",
                    c.getConstructionId(),
                    c.getConstructionName(),
                    c.getAddress(),
                    c.getCreatedAt()));
        }
        offset += 1000;
    } while (!batch.isEmpty());

    writer.flush();
  }

}

