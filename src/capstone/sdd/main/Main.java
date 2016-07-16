package capstone.sdd.main;

import capstone.sdd.core.Settings;
import capstone.sdd.gui.MainFrame;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	
	public static void  main(String[] args) {

		Settings settings = Settings.getInstance();

		settings.addSupported_file("txt");
		settings.addSupported_file("docx");
		settings.addSupported_file("doc");
		settings.addSupported_file("pdf");
		settings.addSupported_file("xlsx");
		settings.addSupported_file("csv");

		settings.setStart_folder("/Users/lieyongzou/Documents");


		SwingUtilities.invokeLater(() -> {
			new MainFrame();
		});

//		Pattern p = Pattern.compile("<html>(.*?)<br>(.*?)</html>");
//		Matcher m = p.matcher("<html>SSN<br>123-45-6789</html>");
//
//		while (m.find()) {
//			System.out.println(m.group(1));
//			System.out.println(m.group(2));
//		}

//		PDDocument doc = new PDDocument();
//		PDPage page1 = new PDPage();
//
//		doc.addPage( page1 );
//
//
//
//		String[][] content = {{"a","b", "1"},
//				{"c","d", "2"},
//				{"e","f", "3"},
//				{"g","h", "4"},
//				{"i","j", "5", "7"}} ;
//
//
//		try {
//			PDPageContentStream contentStream = new PDPageContentStream(doc, page1);
//
//			drawTable(page1, contentStream, 700, 100, content);
//			contentStream.close();
//
//
//
//			doc.save("/Users/lieyongzou/Documents/test.pdf" );
//
//		} catch(IOException e) {
//			e.printStackTrace();
//		}

	}

	private static void drawTable(PDPage page, PDPageContentStream contentStream,
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
