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
		default:
			throw new UnsupportedOperationException();
		}
	}
	
}
