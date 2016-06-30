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

import capstone.sdd.gui.GuiListener;
import capstone.sdd.parser.Parser;
import capstone.sdd.parser.ParserFactory;
import capstone.sdd.validator.Validator;
import capstone.sdd.validator.ValidatorFactory;

/**
 * A class to get the file from Scan worker and parse the file, check the content of file
 * @author lieyongzou
 */
public class MatchWorker implements Callable<Void> {

	// Get the leading and tailing context of sensitive data
	private static final String CONTEXT_PATTERN = ".{0,20}(%s).{0,20}";

	private File file;

	private GuiListener listener;
	
	public MatchWorker(File file, GuiListener listener) {

		this.file = file;
		this.listener = listener;
	}
	
	@Override
	public Void call() throws Exception {
		
		String content = ParserFactory.getContent(file);
		
		// Get a list patterns need to match
		for (Map.Entry<String, Set<String>> entry : Settings.getInstance().getPatterns().entrySet()) {

			// Match the patterns in set
			for (String pattern : entry.getValue()) {
				Pattern p = Pattern.compile(String.format(CONTEXT_PATTERN, pattern));
				Matcher m = p.matcher(content);

				while (m.find()) {

					// Validate the data
					Validator validator = ValidatorFactory.getValidator(entry.getKey());
					if (validator == null || validator.validate(m.group(1))) {
						listener.addResult(entry.getKey(), m.group(1), m.group(0), file);
					} else {
						System.out.println("False data: " + m.group(1));
					}


				}
			}
		}
		
		return null;
	}

}
