package capstone.sdd.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class to scan the whole device
 * @author lieyongzou
 */
public class PIIScanner {
	
	// Settings
	private String start_path;
	private boolean scan_sub_repo = true;
	
	private Set<String> supported_files = new HashSet<>();
	
	public PIIScanner(String start_path){
		this.start_path = start_path;
	}
	
	
	/**
	 * A series of set method to set the setting of scan
	 */
	public void setStartPath(String start_path) {
		this.start_path = start_path;
	}
	
	public void setSubRepo(boolean flag) {
		scan_sub_repo = flag;
	}
	
	/**
	 * A method to add supported file type to scanner, scanner will only scanner those supported type
	 * @param suffix the type of files like .pdf, .txt etc
	 */
	public void addSupportedType(String suffix) {
		supported_files.add(suffix);
	}
	
	/**
	 * A method to scan the device from the start_path
	 * @return a list of file
	 */
	public List<File> scan(){
		List<File> list = new ArrayList<>();
		scanHelper(list, new File(start_path));
		return list;
	}
	
	/**
	 * A method to recursively scan the files in the device
	 * @param list the list that all the files are put in
	 * @param folder the folder
	 */
	private void scanHelper(List<File> list, File folder){
		
		if (folder.isFile()) {
			list.add(folder);
			return;
		}
		
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (isSupported(file)) {
					list.add(file);
				}
				
			} else{
				if (scan_sub_repo) {
					scanHelper(list, file);
				}
			}
		}
	}
	
	
	/**
	 * A method to check whether the current file is supported by parser
	 * @param file the current file
	 * @return true the file can be parsed
	 * 		   false the file cannot be parsed
	 */
	private boolean isSupported(File file) {
		String name = file.getName();
		
		if (supported_files.isEmpty()) {	// By default, all the file type are supported
			return true;
		}
		
		for (String suffix : supported_files) {
			if (name.contains(suffix)) {
				return true;
			}
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		PIIScanner scanner = new PIIScanner("/Users/lieyongzou/Downloads");
		scanner.addSupportedType(".pdf");
		System.out.println(scanner.scan());
	}
}
