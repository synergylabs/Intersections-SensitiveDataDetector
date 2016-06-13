package capstone.sdd.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import capstone.sdd.core.ScanWorker;
import capstone.sdd.core.Settings;

public class Main {
	
	public static void  main(String[] args) {
		
		// Open a executor survice to execute all the tasks by multi-threads
		int POOL_SIZE = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
		
		// Configure the settings, all the workers will have access to the settings
		Settings settings = Settings.getInstance();
		settings.addSupported_file("txt");
		settings.addPattern("(\\d{3}-\\d{2}-\\d{4})");
		settings.setStart_folder("/Users/lieyongzou/Documents");
		
		// Start scan
		pool.submit(new ScanWorker(settings.getStart_folder(), pool));
	}
}
