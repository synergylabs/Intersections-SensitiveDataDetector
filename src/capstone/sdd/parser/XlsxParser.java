package capstone.sdd.parser;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by lieyongzou on 7/6/16.
 */
public class XlsxParser implements Parser{


    @Override
    public String parse(File file) {

        StringBuilder content = new StringBuilder();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(file));

            // Iterate all sheets
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
//            while (sheetIterator.hasNext()) {
//                XSSFSheet sheet = (XSSFSheet) sheetIterator.next();

            HSSFSheet sheet =  workbook.getSheetAt(0);

                // Iterate all rows
                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    HSSFRow row = (HSSFRow) rowIterator.next();

                    // Iterate all cells
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        switch(cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                System.out.println(cell.getStringCellValue());
//                                content.append(cell.getStringCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
//                                content.append(cell.getNumericCellValue() + "");
                                System.out.println(cell.getNumericCellValue());
                        }

                        content.append(" ");
                    }
                    content.append("\n");
                }
                content.append("\n\n");
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public static void main(String[] args) {
        TxtParser parser = new TxtParser();
        File file = new File("/Users/lieyongzou/Documents/Workbook1.xlsx");
        System.out.println(parser.parse(file));
    }
}
