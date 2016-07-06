package capstone.sdd.parser;

import java.io.File;

/**
 * A class for workers to get corresponding parser according to the type of file
 * @author lieyongzou
 *
 */
public class ParserFactory {


	/**
	 * A method to get the content from file
	 * @param file the file
	 * @return the content
     */
	public static String getContent(File file) {

		// Get the extension of file
		int index = file.getName().lastIndexOf('.');
		String suffix = file.getName().substring(index + 1);

		// Get the parser from factory
		Parser parser = ParserFactory.getParser(suffix);
		return " " + parser.parse(file) + " ";
	}



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

			default:
				throw new UnsupportedOperationException();
		}
	}
	
}
