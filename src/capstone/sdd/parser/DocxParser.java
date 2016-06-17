package capstone.sdd.parser;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Created by lieyongzou on 6/16/16.
 */
public class DocxParser implements Parser {

    @Override
    public String parse(File file) {

        StringBuilder content = new StringBuilder();
        try {

            XWPFDocument doc = new XWPFDocument(new FileInputStream(file.getAbsolutePath()));
            List<XWPFParagraph> paras = doc.getParagraphs();

            for (XWPFParagraph para : paras) {
                content.append(para.getText());
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }

        return content.toString();
    }
}
