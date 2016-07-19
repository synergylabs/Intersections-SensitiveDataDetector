package capstone.sdd.core;

import capstone.sdd.gui.GuiListener;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by lieyongzou on 7/10/16.
 */
public class ReportGenerator implements Callable<Void> {

    private String REPORTNAME = "Report%s.pdf";

    // A map <File> -- <Set of sensitive data>
    private Map<File, Set<String>> fileMap;

    // A map <file type> -- <set of file>
    private Map<String, Set<File>> totalFileMap;

    // <data type> - <set of correct data>
    private Map<String, Set<String>> correctDatasetMap;

    // A map <data type> -- <<Data> -- <[context, File path]>>
    private Map<String, Map<String, List<List<String>>>> detailedDatasetMap;

    // The time of some specific operation
    long scanStartTime;
    long scanStopTime;
    long matchStopTime;
    long evalStartTime;
    long evalStopTime;

    private String path;

    private GuiListener listener;


    public ReportGenerator(Map<File, Set<String>> fileMap,
                           Map<String, Set<File>> totalFileMap,
                           Map<String, Set<String>> correctDatasetMap,
                           Map<String, Map<String, List<List<String>>>> detailedDatasetMap,
                           String path, GuiListener listener,
                           long scanStartTime, long scanStopTime, long matchStopTime, long evalStartTime, long evalStopTime) {
        this.fileMap = fileMap;
        this.totalFileMap = totalFileMap;
        this.correctDatasetMap = correctDatasetMap;
        this.detailedDatasetMap = detailedDatasetMap;
        this.path = path;
        this.listener = listener;

        this.scanStartTime = scanStartTime;
        this.scanStopTime = scanStopTime;
        this.matchStopTime = matchStopTime;
        this.evalStartTime = evalStartTime;
        this.evalStopTime = evalStopTime;
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
        Set<String> marks = new HashSet<>();
        for (String datatype : detailedDatasetMap.keySet()) {
            Map<String, List<List<String>>> detailedData = detailedDatasetMap.get(datatype);

            for (String data : correctDatasetMap.get(datatype)) {
                for (List<String> metadata : detailedData.get(data)) {
                    String path = metadata.get(1);

                    if (marks.contains(path)) {
                        continue;
                    }

                    String type = getExtension(path);
                    marks.add(path);

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


        // Data part
        int distinctDataNumber = 0;
        int totalDataNumber = 0;

        // Total number of distinct data
        Map<String, Integer> distinctDataMap = new HashMap<>();
        Map<String, Integer> distinctCorrectDataMap = new HashMap<>();

        // Total number of data which contains same data
        Map<String, Integer> totalDataMap = new HashMap<>();
        Map<String, Integer> totalCorrectDataMap = new HashMap<>();

        for (String datatype : detailedDatasetMap.keySet()) {

            // Add total distinct data
            int localDistinct = detailedDatasetMap.get(datatype).size();
            distinctDataMap.put(datatype, localDistinct);
            distinctDataNumber += localDistinct;

            // Add total correct distinct data
            distinctCorrectDataMap.put(datatype, correctDatasetMap.get(datatype).size());

            int count = 0;
            int countForCorrection = 0;
            Map<String, List<List<String>>> dataMap = detailedDatasetMap.get(datatype);
            for (String data : dataMap.keySet()) {
                count += dataMap.get(data).size();

                if (correctDatasetMap.get(datatype).contains(data)) {
                    countForCorrection += dataMap.get(data).size();
                }
            }

            totalDataMap.put(datatype, count);
            totalDataNumber += count;

            totalCorrectDataMap.put(datatype, countForCorrection);
        }

        System.out.println("There are total " + totalDataNumber + "Sensitive data, and " + distinctDataNumber + " distinct sensitive data");
        for (String type : totalDataMap.keySet()) {
            System.out.println(type + ":  total:  "  + totalDataMap.get(type) + "  correct: " + totalCorrectDataMap.get(type) + " distinct: " + distinctDataMap.get(type) + " distinct correct: " + distinctCorrectDataMap.get(type));
        }

        // Wrap results for file parts
        String[][] contentForFile = new String[totalFileMap.size() + 1][4];
        contentForFile[0][0] = "File Type";
        contentForFile[0][1] = "File Number";
        contentForFile[0][2] = "File with Data";
        contentForFile[0][3] = "True Positive";

        int row = 1;
        for (String type : totalFileMap.keySet()) {
            contentForFile[row][0] = type;
            contentForFile[row][1] = totalFileMap.containsKey(type) ? totalFileMap.get(type).size() + "" : "0";
            contentForFile[row][2] = fileWithData.containsKey(type) ? fileWithData.get(type) + "" : "0";
            contentForFile[row][3] = fileWithCorrectData.containsKey(type) ? fileWithCorrectData.get(type) + "" : "0";

            row += 1;
        }


        // Wrap results for data parts
        String[][] contentForData = new String[totalDataMap.size() + 1][5];
        contentForData[0][0] = "Data Type";
        contentForData[0][1] = "Total";
        contentForData[0][2] = "True Positive";
        contentForData[0][3] = "Distinct";
        contentForData[0][4] = "True Positive";

        row = 1;
        for (String type : totalDataMap.keySet()) {
            contentForData[row][0] = type;
            contentForData[row][1] = totalDataMap.containsKey(type) ? totalDataMap.get(type) + "" : "0";
            contentForData[row][2] = totalCorrectDataMap.containsKey(type) ? totalCorrectDataMap.get(type) + "" : "0";
            contentForData[row][3] = distinctDataMap.containsKey(type) ? distinctDataMap.get(type) + "" : "0";
            contentForData[row][4] = distinctCorrectDataMap.containsKey(type) ? distinctCorrectDataMap.get(type) + "" : "0";

            row += 1;
        }

        List<String> contentForTime = new ArrayList<>();
        contentForTime.add("Scanned takes " + ((scanStopTime - scanStartTime) / 1000) + " seconds.");
        contentForTime.add("Matching takes " + ((matchStopTime - scanStopTime) / 1000) + " seconds.");
        contentForTime.add("Evalatiion of all data takes " + ((evalStopTime - evalStartTime) / 1000) + " seconds");

        // Draw the table to pdf
        PDDocument doc = new PDDocument();
        PDPage page1 = new PDPage();
        PDPage page2 = new PDPage();
        PDPage page3 = new PDPage();
        doc.addPage( page1 );
        doc.addPage( page2 );
        doc.addPage( page3 );


        try {
            PDPageContentStream contentStream = new PDPageContentStream(doc, page1);
            drawTable(page1, contentStream, 700, 50, contentForFile);
            contentStream.close();

            contentStream = new PDPageContentStream(doc, page2);
            drawTable(page2, contentStream, 700, 50, contentForData);
            contentStream.close();

            contentStream = new PDPageContentStream(doc, page3);

            int y = 700;

            for (String line : contentForTime) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.moveTextPositionByAmount(100, y);
                contentStream.drawString(line);
                contentStream.endText();

                y -= 20;
            }

			contentStream.close();

            doc.save(path + "/" + getAvailableName());

        } catch(IOException e) {
            e.printStackTrace();
        }

        listener.finishGenerateReport(path);
        return null;
    }

    /**
     * A method to return the legal name not exist in file system
     * @return the legal name
     */
    private String getAvailableName() {
        int i = 0;
        String name = String.format(REPORTNAME, "");

        File file = new File(path + "/" + name);

        while (file.exists() || file.isDirectory()) {
            i += 1;
            name = String.format(REPORTNAME, "(" + i + ")");
            file = new File(path + "/" + name);
        }

        return name;
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