package capstone.sdd.core;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class store all the settings of scanner
 * @author lieyongzou
 */
public class Settings {

	private static final Pattern DRIVEPATTERN = Pattern.compile("\\((.*?)\\)");
	
	private Set<File> start_folders = new HashSet<>();
	private boolean scan_sub_repo;
	private Set<String> supported_file = new HashSet<>();
	private Map<String, Set<String>> patterns = new HashMap<>();
	private Map<String, Set<String>> customizedPatterns = new HashMap<>();

	private Map<String, Set<String>> mergedPatterns = null;

	public long fileSizeLimit = 5 * 1024 * 1024;
	
	private static Settings instance = null;
	
	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}
	
	/**
	 * A constructor set all the default settings
	 */
	private Settings() {
		scan_sub_repo = true;

		patterns.put("SSN", new HashSet<>());
		patterns.put("CREDIT CARD", new HashSet<>());


		// Init the settings
		patterns.get("SSN").add("(?!000|666)[0-8][0-9]{2}[- ](?!00)[0-9]{2}[- ](?!0000)[0-9]{4}");

		// Visa card
		patterns.get("CREDIT CARD").add("4[0-9]{12}");
		patterns.get("CREDIT CARD").add("4[0-9]{3}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}");

		// Master card
		patterns.get("CREDIT CARD").add("(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)" +
				"[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}");

		// American Expression
		patterns.get("CREDIT CARD").add("3[47][0-9]{2}[- ]{0,1}[0-9]{6}[- ]{0,1}[0-9]{5}");

		// Discover
		patterns.get("CREDIT CARD").add("6(?:011|5[0-9]{2})[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}");

		// Diners Club
		patterns.get("CREDIT CARD").add("3(?:0[0-5]|[68][0-9])[0-9][- ]{0,1}{0,1}[0-9]{6}[- ]{0,1}{0,1}[0-9]{4}");

		// JCB
		patterns.get("CREDIT CARD").add("35[0-9]{2}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}[- ]{0,1}[0-9]{4}");

		// Add supported file type
		supported_file.add("txt");
		supported_file.add("docx");
		supported_file.add("doc");
		supported_file.add("pdf");
		supported_file.add("xlsx");
		supported_file.add("csv");

		// Set start folder
//		start_folders.add(new File("/Users/lieyongzou/Documents"));

		File[] files = File.listRoots();
		for (File file : files) {
			String drive = FileSystemView.getFileSystemView().getSystemDisplayName(file);

			Matcher m = DRIVEPATTERN.matcher(drive);
			if (m.find()) {
				start_folders.add(new File(m.group(1) + "\\"));
			}
		}
	}

	public void setFilesizelimit(int size) {
		fileSizeLimit = size * 1024 * 1024;
	}

	public long getFileSizeLimit() {
		return fileSizeLimit;
	}

	public Set<File> getStart_folder() {
		return start_folders;
	}

	public boolean isScan_sub_repo() {
		return scan_sub_repo;
	}
	
	public boolean isSupported(File file) {
		int index = file.getName().lastIndexOf('.');
		String suffix = file.getName().substring(index + 1);
		
		return supported_file.contains(suffix);
	}


	public Set<String> getSupported_file() {
		return supported_file;
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
			mergedPatterns = new HashMap<>(patterns);

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
