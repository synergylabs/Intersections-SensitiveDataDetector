package capstone.sdd.parser;

import java.io.File;

/**
 * A interface to parse the file inot plain text 
 * @author lieyongzou
 *
 */
public interface Parser {
	
	/**
	 * A method to parse the file
	 * @param file the file
	 * @return the content
	 */
	public String parse(File file);
	
}
