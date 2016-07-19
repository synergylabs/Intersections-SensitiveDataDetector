package capstone.sdd.core;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A class store all the settings of scanner
 * @author lieyongzou
 */
public class Settings {
	
	private File start_folder;
	private boolean scan_sub_repo;
	private Set<String> supported_file = new HashSet<>();
	private Map<String, Set<String>> patterns = new HashMap<>();
	private Map<String, Set<String>> customizedPatterns = new HashMap<>();

	private Map<String, Set<String>> mergedPatterns = null;
	
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
		start_folder = new File("/");
		scan_sub_repo = true;

		patterns.put("SSN", new HashSet<>());
		patterns.put("CREDIT CARD", new HashSet<>());

		patterns.get("SSN").add("(?!000|666)[0-8][0-9]{2}[-* ](?!00)[0-9]{2}[-* ](?!0000)[0-9]{4}");
		patterns.get("CREDIT CARD").add("4[0-9]{12}");
		patterns.get("CREDIT CARD").add("4[0-9]{3}[-* ]{0,1}[0-9]{4}[-* ]{0,1}[0-9]{4}[-* ]{0,1}[0-9]{4}");
	}

	public File getStart_folder() {
		return start_folder;
	}

	public void setStart_folder(String start_path) {
		this.start_folder = new File(start_path);
	}

	public boolean isScan_sub_repo() {
		return scan_sub_repo;
	}
	
	public boolean isSupported(File file) {
		int index = file.getName().lastIndexOf('.');
		String suffix = file.getName().substring(index + 1);
		
		return supported_file.contains(suffix);
	}

	public void addSupported_file(String suffix) {
		this.supported_file.add(suffix);
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
