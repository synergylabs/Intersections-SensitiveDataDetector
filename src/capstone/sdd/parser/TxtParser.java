package capstone.sdd.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A class to parse txt files
 * @author lieyongzou
 *
 */
public class TxtParser implements Parser {

	@Override
	public String parse(File file) {
		StringBuilder sb = new StringBuilder();
		
		// Read the file
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			
			reader.close();
		} catch (IOException e) {
			System.out.println("Lack of permission");
		}
		
		return sb.toString();
	}
	
}
