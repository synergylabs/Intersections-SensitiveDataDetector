package capstone.sdd.core;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class store all the settings of scanner
 * @author lieyongzou
 */
public class Settings {

	// The pattern to extract file id for the same report name, eg: Report(1).pdf
	private static final Pattern DRIVEPATTERN = Pattern.compile("\\((.*?)\\)");

	// The set of start folders
	private Set<File> startFolders = new HashSet<>();

	// The set of supported file extensions
	private Set<String> supportedFileSet = new HashSet<>();

	// The set of our targeted patterns
	private Map<String, Set<String>> patterns = new HashMap<>();

	// The set of customized patterns by users
	private Map<String, Set<String>> customizedPatterns = new HashMap<>();

	// The merged pattern set contains all patterns
	private Map<String, Set<String>> mergedPatterns = null;

	// The threshold for the file to be filtered out
	public long fileSizeLimit = 5 * 1024 * 1024;
	
	private static Settings instance = null;
	
	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	private enum DataType {
		SSN, CREDIT_CARD;

		@Override
		public String toString() {
			return super.toString().replaceAll("_", " ");
		}
	}
	
	/**
	 * A constructor set all the default settings
	 */
	private Settings() {

		patterns.put(DataType.SSN.toString(), new HashSet<>());
		patterns.put(DataType.CREDIT_CARD.toString(), new HashSet<>());


		// Init the settings
		patterns.get(DataType.SSN.toString()).add("(?!000|666)[0-8][0-9]{2}[- ](?!00)[0-9]{2}[- ](?!0000)[0-9]{4}");

		String patternsForCreditCard = "4[0-9]{12}|" +								// visa card
				"4[0-9]{3}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}|" +
				"(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}|" +	// Master
				"3[47][0-9]{2}[- ]{0,1}[0-9]{6}[- ]{0,1}[0-9]{5}|" +	// American Expression
				"6(?:011|5[0-9]{2})[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}|" +		// Discover
				"3(?:0[0-5]|[68][0-9])[0-9][- ]{0,1}{0,1}[0-9]{6}[- ]{0,1}{0,1}[0-9]{4}|" +		// Diners Club
				"35[0-9]{2}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}";		// JCB

		patterns.get(DataType.CREDIT_CARD.toString()).add(patternsForCreditCard);

		// Add supported file type
		supportedFileSet.add("txt");
		supportedFileSet.add("docx");
		supportedFileSet.add("doc");
		supportedFileSet.add("pdf");
		supportedFileSet.add("xlsx");
		supportedFileSet.add("csv");

		// Set start folder
//		startFolders.add(new File("/Users/lieyongzou/Documents"));

		File[] files = File.listRoots();
		for (File file : files) {
			String drive = FileSystemView.getFileSystemView().getSystemDisplayName(file);

			Matcher m = DRIVEPATTERN.matcher(drive);
			if (m.find()) {
				startFolders.add(new File(m.group(1) + "\\"));
			}
		}
	}

	public void setFilesizelimit(int size) {
		fileSizeLimit = size * 1024 * 1024;
	}

	public long getFileSizeLimit() {
		return fileSizeLimit;
	}

	public Set<File> getStartFolder() {
		return startFolders;
	}

	
	public boolean isSupported(File file) {
		int index = file.getName().lastIndexOf('.');
		String suffix = file.getName().substring(index + 1);
		
		return supportedFileSet.contains(suffix);
	}


	public Set<String> getSupportedFileSet() {
		return supportedFileSet;
	}
	
	
	// Regix pattern
	public void addPattern(String name, String pattern) {
		name = name.toLowerCase();
		if (!customizedPatterns.containsKey(name)) {
			customizedPatterns.put(name, new HashSet<>());
		}

		// Replace the space with more delimiter -
		pattern = pattern.replaceAll("\\s", "[ -]{0,1}");

		customizedPatterns.get(name).add(pattern);
		mergedPatterns = null;
	}


	public void removePattern(String name, String pattern) {
		if (customizedPatterns.containsKey(name)) {
			if (customizedPatterns.get(name).contains(pattern)) {
				customizedPatterns.get(name).remove(pattern);
			}
		}
		mergedPatterns = null;
	}

	
	public Map<String, Set<String>> getPatterns() {
		if (mergedPatterns == null) {
			mergedPatterns = new ConcurrentHashMap<>(patterns);

			for (Map.Entry<String, Set<String>> entry : customizedPatterns.entrySet()) {
				if (mergedPatterns.containsKey(entry.getKey())) {
					mergedPatterns.get(entry.getKey()).addAll(entry.getValue());
				} else {
					mergedPatterns.put(entry.getKey(), entry.getValue());
				}
			}
		}

		return mergedPatterns;
	}

	public Map<String, Set<String>> getCustomizedPatterns() {
		return customizedPatterns;
	}

}
