package capstone.sdd.core;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by lieyongzou on 7/10/16.
 */
public class ReportGenerator implements Callable<Void> {

    // A map <File> -- <Set of sensitive data>
    private Map<File, Set<String>> fileMap;

    // A map <file type> -- <set of file>
    private Map<String, Set<File>> totalFileMap;

    // <data type> - <set of correct data>
    private Map<String, Set<String>> correctDatasetMap;

    // A map <data type> -- <<Data> -- <[context, File path]>>
    private Map<String, Map<String, List<List<String>>>> detailedDatasetMap;


    public ReportGenerator(Map<File, Set<String>> fileMap,
                            Map<String, Set<File>> totalFileMap,
                                    Map<String, Set<String>> correctDatasetMap,
                                        Map<String, Map<String, List<List<String>>>> detailedDatasetMap) {
        this.fileMap = fileMap;
        this.totalFileMap = totalFileMap;
        this.correctDatasetMap = correctDatasetMap;
        this.detailedDatasetMap = detailedDatasetMap;
    }


    @Override
    public Void call() throws Exception {

        System.out.println("Start generating report");

        // File part
        int totalFileNumber = 0;
        for (String type : totalFileMap.keySet()) {
            totalFileNumber += totalFileMap.get(type).size();
        }

        // categorize the files
        Map<String, Integer> fileWithData = new HashMap<>();
        for (Map.Entry<File, Set<String>> entry : fileMap.entrySet()) {
            String type = getExtension(entry.getKey().getName());

            if (!fileWithData.containsKey(type)) {
                fileWithData.put(type, 0);
            }

            fileWithData.put(type, fileWithData.get(type) + 1);
        }

        Map<String, Integer> fileWithCorrectData = new HashMap<>();
        for (String datatype : detailedDatasetMap.keySet()) {
            Map<String, List<List<String>>> detailedData = detailedDatasetMap.get(datatype);

            for (String data : correctDatasetMap.get(datatype)) {
                for (List<String> metadata : detailedData.get(data)) {
                    String path = metadata.get(1);
                    String type = getExtension(path);

                    if (!fileWithCorrectData.containsKey(type)) {
                        fileWithCorrectData.put(type, 0);
                    }

                    fileWithCorrectData.put(type, fileWithCorrectData.get(type) + 1);
                }
            }
        }

        System.out.println("Total number of files:" + totalFileNumber);
        for (String type : totalFileMap.keySet()) {
            System.out.println(type + ":    "  + totalFileMap.get(type).size() + "    " + fileWithData.get(type) + "    " + fileWithCorrectData.get(type));
        }

        return null;
    }



    private String getExtension(String name) {

        // Get the extension of file
        int index = name.lastIndexOf('.');
        String suffix = name.substring(index + 1);

        return suffix.toLowerCase();
    }



    /**
     * @param page
     * @param contentStream
     * @param y the y-coordinate of the first row
     * @param margin the padding on left and right of table
     * @param content a 2d array containing the table data
     * @throws IOException
     */
    private void drawTable(PDPage page, PDPageContentStream contentStream,
                                 float y, float margin,
                                 String[][] content) throws IOException {
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth() - margin - margin;
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        //draw the rows
        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(margin, nexty, margin+tableWidth, nexty);
            nexty-= rowHeight;
        }

        //draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx, y, nextx, y-tableHeight);
            nextx += colWidth;
        }

        //now add the text
        contentStream.setFont( PDType1Font.HELVETICA_BOLD , 12 );

        float textx = margin+cellMargin;
        float texty = y-15;
        for(int i = 0; i < content.length; i++){
            for(int j = 0 ; j < content[i].length; j++){
                String text = content[i][j];
                contentStream.beginText();
                contentStream.moveTextPositionByAmount(textx,texty);
                contentStream.drawString(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty-=rowHeight;
            textx = margin+cellMargin;
        }
    }
}
