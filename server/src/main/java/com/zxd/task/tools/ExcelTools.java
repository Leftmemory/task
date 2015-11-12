package com.zxd.task.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by zxd on 15/11/12.
 */
@Slf4j
public class ExcelTools {

    public static void exportExcel(String fileName, Map<String, String> map) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        //产生工作表对象
        HSSFSheet sheet = workbook.createSheet();
        int i = 0;
        for (Map.Entry<String, String> e : map.entrySet()) {
            HSSFRow row = sheet.createRow((int) i);//创建一行
            HSSFCell cell = row.createCell((int) 0);//创建一列
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(e.getKey());
            HSSFCell cell2 = row.createCell((int) 1);//创建第二列
            cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell2.setCellValue(e.getValue());
            i++;
        }
        try {
            log.info("开始写入文件" + fileName);
            OutputStream fos = new FileOutputStream(fileName);
            workbook.write(fos);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
