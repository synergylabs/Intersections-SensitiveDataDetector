package capstone.sdd.parser;

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
