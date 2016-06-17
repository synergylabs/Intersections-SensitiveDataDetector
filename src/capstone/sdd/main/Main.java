package capstone.sdd.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import capstone.sdd.core.ScanWorker;
import capstone.sdd.core.Settings;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;


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
		settings.setScan_sub_repo(false);
		settings.addPattern("(\\d{3}-\\d{2}-\\d{4})");
		settings.setStart_folder("/Users/lieyongzou/Documents");

		// Start scan
		pool.submit(new ScanWorker(settings.getStart_folder(), pool));

//		SwingUtilities.invokeLater(() -> new MainFrame());

	}

}
