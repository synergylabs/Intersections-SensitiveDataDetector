package capstone.sdd.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class to scan the whole device
 * @author lieyongzou
 */
public class PIIScanner {
	
	// Settings
	private Settings settings = Settings.getInstance();
	
	private final int POOL_SIZE = Runtime.getRuntime().availableProcessors();
	private final ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

	
	/**
	 * A method to scan the device from the start_path
	 * @return a list of file
	 */
	public List<File> scan(){
		List<File> list = new ArrayList<>();
//		scanHelper(list, settings.getStart_folder());
		pool.submit(new ScanWorker(settings.getStart_folder()));
		return list;
	}
	
	/**
	 * A method to recursively scan the files in the device
	 * @param list the list that all the files are put in
	 * @param folder the folder
	 */
	private void scanHelper(List<File> list, File folder){

		try {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file == null) {
					continue;
				}
					
				if (file.isDirectory()) {
					if (settings.isScan_sub_repo()) {
						scanHelper(list, file);
					}
				} else{
					if (settings.isSupported(file)) {
						System.out.println(file.getName());
						list.add(file);
					}
				}
			}
		} catch(Exception e) {
			System.out.println(folder.getAbsolutePath());
		}
	}
	
	
	
	private class ScanWorker implements Callable<List<File>> {
		
		private File folder;
		
		public ScanWorker(File folder) {
			this.folder = folder;
		}

		@Override
		public List<File> call() throws Exception {
			List<File> list = new ArrayList<>();
			
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file == null) {
					continue;
				}
					
				if (file.isDirectory()) {
					if (settings.isScan_sub_repo()) {
						scanHelper(list, file);
					}
				} else{
					if (settings.isSupported(file)) {
						System.out.println(file.getName());
						list.add(file);
					}
				}
			}
			return list;
		}
		
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		PIIScanner scanner = new PIIScanner();
		Settings settings = Settings.getInstance();
		settings.addSupported_file("txt");
		settings.setStart_folder("/Users/lieyongzou/Documents");
		
		System.out.println(scanner.scan().size());
		long end = System.currentTimeMillis();
		System.out.println(end - start);
	}
}
