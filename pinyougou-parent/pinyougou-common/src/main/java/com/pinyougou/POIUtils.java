package com.pinyougou;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.capitalize;

public class POIUtils {
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";
    private final static String DATE_FORMAT = "yyyy/MM/dd";

    /**
     * excel导出 默认输出全部字段
     *
     * @param list 数据集合
     * @return 输出
     * @throws Exception 错误
     */
    public static <T> ByteArrayOutputStream exportExcel(List<T> list) throws Exception {
        return exportExcel(list, new String[]{"SerialVersionUID"});
    }

    /**
     * excel导出
     *
     * @param list         数据集合
     * @param delFieldName 需要排除的字段
     * @return 输出
     * @throws Exception 错误
     */
    public static <T> ByteArrayOutputStream exportExcel(List<T> list, String[] delFieldName) throws Exception {

        XSSFWorkbook wb = new XSSFWorkbook();

        T t = list.get(0);
        if (t == null) {
            throw new RuntimeException("List must to have value");
        }
        Class<?> clazz = t.getClass();

        Field[] fields = clazz.getDeclaredFields();
        List<Integer> delFieldList = new ArrayList<>();
        //设置排除的字段
        if (delFieldName != null && delFieldName.length > 0) {
            for (String del : delFieldName) {
                for (int i = 0; i < fields.length; i++) {
                    if (fields[i].getName().equalsIgnoreCase(del)) {
                        delFieldList.add(i);
                    }
                }
            }
        }
        XSSFSheet sheet = wb.createSheet(clazz.getName());//用类的名字
        // 设置表头的说明
        XSSFRow topRow = sheet.createRow(0);
        title:
        for (int i = 0; i < fields.length; i++) {
            for (Integer del : delFieldList) {
                if (del.equals(i)) break title;
            }
            setCellType(topRow.createCell(i), fields[i].getName());
        }

        String methodName;
        Method method = null;
        Object ret;
        // 遍历生成数据行，通过反射获取字段的get方法
        for (int i = 0; i < list.size(); i++) {
            t = list.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            cell:
            for (int j = 0; j < fields.length; j++) {
                for (Integer del : delFieldList) {
                    if (del.equals(j)) continue cell;
                }

                methodName = "get" + capitalize(fields[j].getName());
                try {
                    method = clazz.getDeclaredMethod(methodName);
                } catch (java.lang.NoSuchMethodException e) {    //	不存在该方法，查看父类是否存在。此处只支持一级父类，若想支持更多，建议使用while循环
                    if (null != clazz.getSuperclass()) {
                        method = clazz.getSuperclass().getDeclaredMethod(methodName);

                    }
                }
                if (null == method) {
                    throw new Exception(clazz.getName() + " don't have menthod --> " + methodName);
                }
                ret = method.invoke(t);
                /*if (fields[i].getType().getName().equals("java.util.Date")) {
                    ret = new SimpleDateFormat(DATE_FORMAT).format(ret);
                }*/
                if (ret == null) ret = "";
                setCellType(row.createCell(j), ret + "");
            }
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
        wb.write(os);
        return os;
    }

    private static void setCellType(XSSFCell cell, String value) {
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value);
    }

    /**
     * 读入excel文件，解析后返回
     *
     * @param file     文件流
     * @param fileName 文件名
     * @param clazz    输出类型
     * @param <T>      由class决定
     * @return 集合
     * @throws Exception 问题
     */
    public static <T> List<T> readExcel(InputStream file, String fileName, Class<T> clazz) throws Exception {
        //检查文件
        checkFile(file, fileName);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file, fileName);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<T> list = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                // 获得表头总列数
                int cols = sheet.getRow(0).getPhysicalNumberOfCells();

                //循环除了第一行的所有行
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    T t = clazz.newInstance();
                    // 遍历该行所有列
                    for (int j = 0; j < cols; j++) {
                        Object value = null;
                        Cell cell = row.getCell(j);
                        if (null == cell) continue;    // 为空时，下一列
                        Object cellValue = getCellValue(cell);
                        if(cellValue==null||cellValue=="")continue;
                        String methodName = "set" + capitalize(fields[j].getName());
                        Method method = null;
                        try {
                            method = clazz.getDeclaredMethod(methodName,fields[j].getType());
                        } catch (NoSuchMethodException e) {    //	不存在该方法，查看父类是否存在。此处只支持一级父类，若想支持更多，建议使用while循环
                            if (null != clazz.getSuperclass()) {
                                method = clazz.getSuperclass().getDeclaredMethod(methodName);
                            }
                        }
                        if (null == method) {
                            throw new Exception(clazz.getName() + " don't have method --> " + methodName);
                        }
                        method.invoke(t, cellValue);
                    }
                    list.add(t);
                }
            }
            workbook.close();
        }
        return list;
    }

    //校验文件是否合法
    private static void checkFile(InputStream file, String fileName) throws IOException {
        //判断文件是否存在
        if (null == file) {
            throw new FileNotFoundException("文件不存在！");
        }
        //判断文件是否是excel文件
        if (!fileName.endsWith(xls) && !fileName.endsWith(xlsx)) {
            throw new IOException(fileName + "不是excel文件");
        }
    }

    private static Workbook getWorkBook(InputStream file, String fileName) {
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(xls)) {
                //2003
                workbook = new HSSFWorkbook(file);
            } else if (fileName.endsWith(xlsx)) {
                //2007
                workbook = new XSSFWorkbook(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    private static Object getCellValue(Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }
        Object cellValue;
        //如果当前单元格内容为日期类型，需要特殊处理
        String dataFormatString = cell.getCellStyle().getDataFormatString();
        if (dataFormatString.equals("m/d/yy")) {
            cellValue = cell.getDateCellValue();
            return cellValue;
        }
        //判断数据的类型
        switch (cell.getCellTypeEnum()) {
            case NUMERIC: //数字
                cellValue = cell.getNumericCellValue();
                if (cell.getCellStyle().getDataFormat() > 0) {
                    cellValue = cell.getDateCellValue();
                }
                break;
            case STRING: //字符串
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN: //Boolean
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA: //公式
                cellValue = cell.getCellFormula();
                break;
            case BLANK: //空值
                cellValue = null;
                break;
            case ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
}
