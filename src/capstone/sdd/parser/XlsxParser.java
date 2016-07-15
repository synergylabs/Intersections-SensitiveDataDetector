package capstone.sdd.parser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
            FileInputStream fis = new FileInputStream(file);

            XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIterator = mySheet.iterator();

            while (rowIterator.hasNext()) {

                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            content.append(cell.getStringCellValue() + " ");
                            break;

                        case Cell.CELL_TYPE_NUMERIC:
                            content.append(cell.getNumericCellValue() + " ");
                            break;

                    }
                }

                content.append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

}
