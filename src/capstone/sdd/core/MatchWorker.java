/**
 * 
 */
package capstone.sdd.core;

import java.io.File;
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
		for (String pattern : Settings.getInstance().getPatterns()) {
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(content);
			
			if (m.find()) {
				System.out.println(m.group(0));
				System.out.println(file.getAbsolutePath());
			}
		}
		
		return null;
	}

}
