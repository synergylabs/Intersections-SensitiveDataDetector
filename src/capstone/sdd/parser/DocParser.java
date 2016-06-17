package capstone.sdd.parser;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.HWPFDocument;

/**
 * Created by lieyongzou on 6/16/16.
 */
public class DocParser implements Parser {

    @Override
    public String parse(File file) {
        WordExtractor extractor = null;
        StringBuilder content = new StringBuilder();

        try {
            HWPFDocument doc = new HWPFDocument(new FileInputStream(file));
            extractor = new WordExtractor(doc);
            String[] lines = extractor.getParagraphText();

            for (String line : lines) {
                content.append(line);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void main(String[] args) {
        Parser parser = new DocParser();
        File file = new File("/Users/lieyongzou/Documents/word.docx");
        System.out.println(parser.parse(file));
    }

}
