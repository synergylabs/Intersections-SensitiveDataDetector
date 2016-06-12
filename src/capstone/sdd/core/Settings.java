package capstone.sdd.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A class store all the settings of scanner
 * @author lieyongzou
 */
public class Settings {
	
	private File start_folder;
	private boolean scan_sub_repo;
	private List<String> supported_file = new ArrayList<>();
	private List<String> patterns = new ArrayList<>();
	
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

	public void setScan_sub_repo(boolean scan_sub_repo) {
		this.scan_sub_repo = scan_sub_repo;
	}

//	public List<String> getSupported_file() {
//		return supported_file;
//	}
	
	public boolean isSupported(File file) {
		int index = file.getName().lastIndexOf('.');
		String suffix = file.getName().substring(index + 1);
		
		return supported_file.contains(suffix);
	}

	public void addSupported_file(String suffix) {
		this.supported_file.add(suffix);
	}
	
	
	public void addPattern(String pattern) {
		patterns.add(pattern);
	}
	
	public void removePattern(String pattern) {
		patterns.remove(pattern);
	}
}
