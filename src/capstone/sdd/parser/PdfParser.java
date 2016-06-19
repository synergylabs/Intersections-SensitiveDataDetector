package capstone.sdd.parser;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 * Created by lieyongzou on 6/18/16.
 */
public class PdfParser implements Parser {

    @Override
    public String parse(File file) {
        String content = null;
        PDDocument doc = null;
        try {

            doc = PDDocument.load(file);
            doc.getClass();
            if (!doc.isEncrypted()) {
                PDFTextStripperByArea stripperArea = new PDFTextStripperByArea();
                stripperArea.setSortByPosition(true);
                PDFTextStripper stripper = new PDFTextStripper();
                content = stripper.getText(doc);
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        return content;
    }

}
