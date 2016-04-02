package com.zxd.task.tools;

import com.google.common.collect.Lists;
import com.zxd.task.mapper.RedisLimitHashMapper;
import com.zxd.task.model.RedisLimitHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zxd on 15/11/12.
 */
@Component
@Slf4j
public class ExcelTools {


    @Autowired
    private RedisLimitHashMapper redisLimitHashMapper;

    public void exportExcel(String fileName, Map<String, String> map) {
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

    public void transformExcel2Table(String fileName, Integer actId) {
        try {
            InputStream is = new FileInputStream(fileName);
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            List<RedisLimitHash> limitHashList = Lists.newArrayList();


            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = workbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
                // Read the Row
                int i = 0;
                for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow != null) {
                        Cell userId = hssfRow.getCell(0);
                        Cell goodsId = hssfRow.getCell(1);
                        Cell time = hssfRow.getCell(2);
                        Cell number = hssfRow.getCell(3);
                        RedisLimitHash limitHash = new RedisLimitHash();
                        limitHash.setUserId(Integer.parseInt(getCellValue(userId)));
                        limitHash.setGoodsId(Integer.parseInt(getCellValue(goodsId)));
                        limitHash.setTime(dateTransform(getCellValue(time)));
                        limitHash.setNumber(Integer.parseInt(getCellValue(number)));
                        limitHash.setType(actId);
                        limitHashList.add(limitHash);
                        i++;
                        if (i == 100) {
                            i = 0;
                            redisLimitHashMapper.batchInsertSelective(limitHashList.get(0), limitHashList);
                            limitHashList = Lists.newArrayList();
                        }
                    }
                    if (!CollectionUtils.isEmpty(limitHashList)) {
                        redisLimitHashMapper.batchInsertSelective(limitHashList.get(0), limitHashList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static String getValue(XSSFCell xssfRow) {
//        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
//            return String.valueOf(xssfRow.getBooleanCellValue());
//        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
//            return String.valueOf(xssfRow.getNumericCellValue());
//        } else {
//            return String.valueOf(xssfRow.getStringCellValue());
//        }
//    }
//
//    private static String getValue(HSSFCell hssfCell) {
//        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
//            return String.valueOf(hssfCell.getBooleanCellValue());
//        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
//            return String.valueOf(hssfCell.getNumericCellValue());
//        } else {
//            return String.valueOf(hssfCell.getStringCellValue());
//        }
//    }


    private static String getCellValue(Cell cell) {
        String cellValue;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = Boolean.toString(cell.getBooleanCellValue());
                break;
            // 数值
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = String.valueOf(cell.getDateCellValue());
                } else {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String temp = cell.getStringCellValue();
                    // 判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串（也可以使用BigDecimal，但是从Excel里面读取出来的数字，有一些计算的值，会不精确，这里四舍五入一次，根据需要自己修改吧）
                    if (temp.indexOf(".") > -1) {
                        Double d = new Double(temp);
                        cellValue = new DecimalFormat("#.00").format(d);
                    } else {
                        cellValue = temp.trim();
                    }
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue().trim();
                break;
            case Cell.CELL_TYPE_ERROR:
                cellValue = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cellValue = cell.getStringCellValue();
                if (cellValue != null) {
                    cellValue = cellValue.replaceAll("#N/A", "").trim();
                }
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }

    private static String dateTransform(String time) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date d = sdf.parse(time);
        sdf.applyPattern("yyyy-MM-dd");
        return sdf.format(d);
    }
}
