package capstone.sdd.parser;

import java.io.File;

/**
 * A class for workers to get corresponding parser according to the type of file
 * @author lieyongzou
 *
 */
public class ParserFactory {

	public static Parser getParser(String type) {
		type = type.toLowerCase();
		
		switch(type) {
			case "txt":
			case "csv":
				return new TxtParser();

			case "doc":
				return new DocParser();

			case "docx":
				return new DocxParser();

			case "pdf":
				return new PdfParser();

			case "xlsx":
				return new XlsxParser();

			default:
				throw new UnsupportedOperationException();
		}
	}
	
}
