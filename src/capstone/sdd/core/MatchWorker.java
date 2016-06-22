/**
 * 
 */
package capstone.sdd.core;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import capstone.sdd.parser.Parser;
import capstone.sdd.parser.ParserFactory;

/**
 * A class to get the file from Scan worker and parse the file, check the content of file
 * @author lieyongzou
 */
public class MatchWorker implements Callable<Void> {

	// Get the leading and tailing context of sensitive data
	private static final String CONTEXT_PATTERN = ".{0,20}%s.{0,20}";

	private File file;
	private Parser parser;
	
	public MatchWorker(File file) {
		
		// Get the extension of file
		int index = file.getName().lastIndexOf('.');
		String suffix = file.getName().substring(index + 1);
		
		// Get the parser from factory
		parser = ParserFactory.getParser(suffix);
		this.file = file;
	}
	
	@Override
	public Void call() throws Exception {
		
		String content = parser.parse(file);
		
		// Get a list patterns need to match
		for (Map.Entry<String, Set<String>> entry : Settings.getInstance().getPatterns().entrySet()) {

			// Match the patterns in set
			for (String pattern : entry.getValue()) {
				Pattern p = Pattern.compile(String.format(CONTEXT_PATTERN, pattern));
//				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(content);

				while (m.find()) {
					System.out.println("Data Type: " + entry.getKey());
					System.out.println("Data: " + m.group());
					System.out.println("Files: " + file.getAbsolutePath() + "\n");
				}
			}

			

		}
		
		return null;
	}

}
