package capstone.sdd.main;

import capstone.sdd.core.Settings;
import capstone.sdd.gui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	
	public static void  main(String[] args) {

		// Open a executor survice to execute all the tasks by multi-threads
//		int POOL_SIZE = Runtime.getRuntime().availableProcessors();
//		ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
//
		// Configure the settings, all the workers will have access to the settings
		Settings settings = Settings.getInstance();
//		settings.setScan_sub_repo(false);

		settings.addSupported_file("txt");
		settings.addSupported_file("docx");
		settings.addSupported_file("doc");
		settings.addSupported_file("pdf");
		settings.addSupported_file("csv");

		settings.addPattern("ssn", "(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}");
		settings.addPattern("credit card", "4[0-9]{12}(?:[0-9]{3})?");
		settings.addPattern("credit card", "4[0-9]{3} [0-9]{4} [0-9]{4} [0-9]{4}");

		settings.setStart_folder("/Users/lieyongzou/Documents");


		SwingUtilities.invokeLater(() -> {
			new MainFrame();
		});

	}

}
