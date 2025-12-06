package com.itflower.aiplatform;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;

@Slf4j
@SpringBootTest
public class excelTest {

    @Test
    public void excelTest() throws Exception {
        if (!Files.exists(new File("src/test/java/com/itflower/aiplatform/copied.xlsx").toPath())) {
            Files.createFile(new File("src/test/java/com/itflower/aiplatform/copied.xlsx").toPath());
            FileUtils.copyFile(
                    new File("src/test/java/com/itflower/aiplatform/案件入参字段模板-TO 机构.xlsx"),
                    new File("src/test/java/com/itflower/aiplatform/copied.xlsx")
            );
        }
        FileInputStream fileInputStream = new FileInputStream("src/test/java/com/itflower/aiplatform/copied.xlsx");
        Workbook sheets = new XSSFWorkbook(fileInputStream);
        Sheet sheet = sheets.getSheetAt(3);
        sheet.createRow(5);
        Row row = sheet.getRow(5);
        row.createCell(0);
        row.getCell(0).setCellValue("114514");
        FileOutputStream fileOutputStream = new FileOutputStream("src/test/java/com/itflower/aiplatform/copied.xlsx");
        sheets.write(fileOutputStream);
        fileOutputStream.close();
        fileInputStream.close();
        sheets.close();
    }
}
