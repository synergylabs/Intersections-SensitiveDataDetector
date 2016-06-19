package capstone.sdd.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import capstone.sdd.core.ScanWorker;
import capstone.sdd.core.Settings;
import capstone.sdd.gui.MainFrame;

import javax.swing.*;


public class Main {

	
	public static void  main(String[] args) {

		// Open a executor survice to execute all the tasks by multi-threads
		int POOL_SIZE = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

		// Configure the settings, all the workers will have access to the settings
		Settings settings = Settings.getInstance();
		settings.addSupported_file("txt");
		settings.addSupported_file("docx");
		settings.addSupported_file("doc");
		settings.addSupported_file("pdf");
		settings.setScan_sub_repo(false);

		settings.addPattern("ssn", "(\\d{3}-\\d{2}-\\d{4})");
		settings.addPattern("credit card", "4[0-9]{12}(?:[0-9]{3})?");
		settings.addPattern("credit card", "4[0-9]{3} [0-9]{4} [0-9]{4} [0-9]{4}");
		settings.setStart_folder("/Users/lieyongzou/Documents");

		// Start scan
		pool.submit(new ScanWorker(settings.getStart_folder(), pool));

//		SwingUtilities.invokeLater(() -> new MainFrame());
	}

}
