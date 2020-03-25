package org.qadoc.atool.groovy.excel

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference

import java.nio.file.Files;

/**
 * Excel工具类，基于Apache POI
 *
 * @author liyun
 * 2017年9月25日
 */
class ExcelPOIUtil {

    //----------------------------Workbook操作----------------------------------------

    /**
     * 打开工作薄，返回工作薄对象。
     *
     * <p>以文件流的方式打开，支持{@link ExcelPOIUtil#save(Workbook, String)}方法，支持.xls和.xlsx文件。</p>
     *
     * <p>该方法相比{@link ExcelPOIUtil#openReadOnly(String)}，会有更高的内存占用。如果只读取不写入，建议使用后者。
     * 为了正确释放资源，在使用完工作薄对象后，应调用{@link ExcelPOIUtil#close(Workbook)}方法。</p>
     *
     * @param pathname 要打开的Excel文件路径：相对路径文件名或者完整路径文件名。
     * @return Excel文件对应的工作薄对象。
     */
    Workbook open(String pathname) {
        //创建工作薄，一个Excel文件对应一个工作薄，一个工作薄对应多个工作表
        Workbook wb;
        File file = new File(pathname);
        String path = file.getAbsolutePath();
        FileInputStream input = null;
        BufferedInputStream bis = null;
        try {
            input = new FileInputStream(file);
            bis = new BufferedInputStream(input);
            //打开工作薄：支持读取.xls和.xlsx文件
            wb = WorkbookFactory.create(bis);
        } catch (IOException e) {
            throw new RuntimeException("read excel file fail, the file path is：" + path,e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("the content of excel can not transfer to  Workbook object, filename is：" + path,e);
        }finally {
            if (input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    throw new RuntimeException("close FileInputStream fail, the file path is：" + path,e);
                }
            }
            if (bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    throw new RuntimeException("close BufferedInputStream fail, the file path is：" + path,e);
                }
            }
        }
        return wb;
    }

    /**
     * 打开工作薄，返回工作薄对象。
     *
     * <p>以文件的方式打开，有限支持{@link ExcelPOIUtil#save(Workbook, String)}方法（不能保存到原来的文件，但可以保存到新的文件，
     * 因为源文件处于打开状态，和工作薄对象直接关联，所以不能对源文件进行写入操作，除非先关闭工作薄对象），
     * 支持.xls和.xlsx文件。</p>
     *
     * <p>该方法相比{@link ExcelPOIUtil#open(String)}，会有更低的内存占用。如果只读取不写入，建议使用该方法。
     * 为了正确释放资源，在使用完工作薄对象后，应调用{@link ExcelPOIUtil#close(Workbook)}方法。</p>
     *
     * @param pathname 要打开的Excel文件路径：相对路径文件名或者完整路径文件名。
     * @return Excel文件对应的工作薄对象。
     */
    Workbook openReadOnly(String pathname) {
        //创建工作薄，一个Excel文件对应一个工作薄，一个工作薄对应多个工作表
        Workbook wb;
        File file = new File(pathname);
        String path = file.getAbsolutePath();
        try {
            //打开工作薄：支持读取.xls和.xlsx文件
            wb = WorkbookFactory.create(file);
        } catch (IOException e) {
            throw new RuntimeException("read excel file fail, the file path is：" + path,e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException("the content of excel can not transfer to  Workbook object, filename is：" + path,e);
        }
        return wb;
    }

    /**
     * 保存工作薄，保存到指定文件。
     *
     * <p>支持.xls和.xlsx文件。</p>
     *
     * <p>为了正确释放资源，在使用完工作薄对象后，应调用{@link ExcelPOIUtil#close(Workbook)}方法。</p>
     *
     * @param wb       要保存的工作薄
     * @param pathname 保存工作薄的文件路径
     */
    void save(Workbook wb, String pathname) {
        if(wb.getNumberOfSheets() == 0){
            throw new RuntimeException("the content of file is empty, but the workbook need a sheet at least, please check your code!");
        }
        def file = new File(pathname);
        def path = file.getAbsolutePath();
        //是否是保存到已存在的文件
        def  isSaveExists = false;
        //如果是保存到已存在的文件，进行错误回滚处理准备，备份原文件
        def backupFile = null;
        if(file.exists()){
            isSaveExists = true;
            def backupFilePath = pathname.replaceAll(file.getName(),"")+"Backup_"+file.getName();
            backupFile = new File(backupFilePath);
            try {
                Files.copy(file.toPath(), backupFile.toPath());
            } catch (IOException e) {
                if(backupFile.exists()){
                    backupFile.delete();
                }
                throw new RuntimeException("backup file fail, the origin file path is: " + path,e);
            }
        }
        //保存工作薄
        def out = null;
        try {
            //新建输出流
            out = new FileOutputStream(pathname);
            //将工作薄内容写入到输出流
            wb.write(out);
            //如果正常保存，删除备份文件
            if(backupFile.exists()){
                backupFile.delete();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file path is invalid, file path is: " + path,e);
        } catch (Exception e) {
            //尝试使用wb.close()和System.gc()方法解除文件占用，尝试失败。
            //写入文件失败后，无法解除文件占用，所以不能删除占用的文件，以下注释代码无法正常执行
            /*//删除写入未完成的新建文件或已损坏的原文件
            if(file.exists()){
                try {
                    Files.delete(file.toPath());
                } catch (Exception e1) {
                    if(isSaveExists){
                        throw new RuntimeException("恢复文件出错，无法删除损坏的原文件，原文件路径："+path,e1);
                    }
                    throw new RuntimeException("删除写入未完成的新建文件出错，新建文件路径："+path,e1);
                }
            }
            //如果是保存到已存在的文件，恢复原文件
            if(isSaveExists){
                //通过备份文件恢复原文件
                if(backupFile != null && backupFile.exists()){
                    backupFile.renameTo(new File(pathname));
                }else{
                    throw new RuntimeException("备份文件被误删，无法进行失败回滚！原文件路径："+path,e);
                }
            }*/
            //其他解决方法：如果使用open方法也有此异常，请尝试使用Excel程序打开文件，编辑保存后，重新运行代码。
            throw new RuntimeException("the write operate of workbook fail. If you are writing something to origin file, it maybe damage. You can find a backup file named 'Backup_+originFileName'. The reason of fail maybe you used openReadOnly method. The file path is: " + path,e);
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("close FileOutputStream fail, the file path is: " + path,e);
            }
        }
    }

    /**
     * 关闭工作薄
     *
     * @param wb 要关闭的工作薄
     */
    void close(Workbook wb) {
        try {
            if(wb != null){
                wb.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("close workbook fail!",e);
        }
    }

    /**
     * 保存并关闭工作薄，保存到指定文件。
     *
     * <p>支持.xls和.xlsx文件。</p>
     *
     * @param wb       要操作的工作薄
     * @param pathname 保存工作薄的文件路径
     */
    void saveAndClose(Workbook wb, String pathname) {
        save(wb, pathname);
        close(wb);
    }

    //----------------------------Sheet操作----------------------------------------

    /**
     * 移除工作表：根据工作表名称（工作薄中工作表名称不允许重复）
     *
     * @param wb        工作薄
     * @param sheetName 工作表名称，不区分大小写
     */
    void removeSheet(Workbook wb, String sheetName) {
        Sheet sheet = wb.getSheet(sheetName);
        removeSheet(wb, sheet);
    }

    /**
     * 移除工作表：根据工作表对象
     *
     * @param wb    工作薄
     * @param sheet 工作表
     */
    void removeSheet(Workbook wb, Sheet sheet) {
        if (sheet != null) {
            int index = wb.getSheetIndex(sheet);
            wb.removeSheetAt(index);
        }
    }

    //----------------------------Row操作----------------------------------------

    /**
     * 获取指定Sheet页，指定行的内容。
     * @param sheet Sheet对象，指定Sheet页
     * @param rowindex 行索引，从0开始
     */
    List<String> getStringRowValue(Sheet sheet,int rowindex){
        List<String> headers = new ArrayList<String>()
        def row = sheet.getRow(rowindex)
        for(def j =0; j < row.getLastCellNum(); j++){
            def cell = row.getCell(j)
            headers.add(cell.getStringCellValue())
        }
        return headers
    }

    //----------------------------Cell操作----------------------------------------

    /**
     * 获取单元格对象，根据给定工作表的给定单元格地址。
     *
     * @param sheet 工作表
     * @param cellAddr 单元格地址：A1,B3
     * @return 单元格对象
     */
    Cell getCell(Sheet sheet, String cellAddr) {
        CellReference ref = new CellReference(cellAddr);
        Row row = sheet.getRow(ref.getRow());
        Cell cell = row.getCell(ref.getCol());
        return cell;
    }

    /**
     * 获取单元格地址
     * @param cell 单元格
     * @return 单元格地址，如“B1”
     */
    String getCellAddress(Cell cell){
        CellReference cellReference = new CellReference(cell);
        return cellReference.formatAsString();
    }

        /**
      * 获取单元格的值
      * @param cell 单元格
      * @return 单元格的值，如果为空或null返回空
      */
    String getStringCellValue(Cell cell){
        if(cell == null) return "";
        switch(cell.getCellType()){
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_BLANK:
                return "";
        }
        return "";
     }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet  工作表
     * @param row    行号
     * @param column 列号
     * @return 如果是合并单元格返回True，否则返回False
     */
    boolean isMergedRegion(Sheet sheet, int row, int column) {
        return getMergedRegionIndex(sheet, row, column) != null;
    }

    /**
     * 获取合并单元格的起始结束行号和列号
     *
     * @param sheet  工作表
     * @param row    合并单元格内的任一行号
     * @param column 合并单元格内的任一列号
     * @return 合并单元格的起始结束行号和列号，依次为：起始行号、结束行号、起始列号、结束列号。如果不是合并单元格返回NULL。
     */
    int[] getMergedRegionIndex(Sheet sheet, int row, int column) {
        int[] index = new int[4];
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    index[0] = firstRow;
                    index[1] = lastRow;
                    index[2] = firstColumn;
                    index[3] = lastColumn;
                    return index;
                }
            }
        }
        return null;
    }

    /**
     * 获取合并单元格的合并行数
     *
     * @param sheet  工作表
     * @param row    行号，合并单元格的任一行号
     * @param column 列号，合并单元格的任一列号
     * @return 合并单元格的合并行数
     */
    int getMergedRegionRowCount(Sheet sheet, int row, int column) {
        int[] index = getMergedRegionIndex(sheet, row, column);
        return index[1] - index[0] + 1;
    }

}
