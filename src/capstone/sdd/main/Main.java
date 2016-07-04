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

		settings.addPattern("ssn", "[\\D](?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}[\\D]");
		settings.addPattern("credit card", "[\\D]4[0-9]{12}(?:[0-9]{3})?[\\D]");
		settings.addPattern("credit card", "[\\D]4[0-9]{3} [0-9]{4} [0-9]{4} [0-9]{4}[\\D]");

		settings.setStart_folder("/Users/lieyongzou/Documents");
//
//		// Start scan
//		pool.submit(new ScanWorker(settings.getStart_folder(), pool));

		SwingUtilities.invokeLater(() -> {
			new MainFrame();
//			NewClass();
		});


//		Pattern p = Pattern.compile("[\\D](4[0-9]{3})");
//		Matcher m = p.matcher("4221=1231273y182");
//		while (m.find()) {
//			System.out.print(m.group(1));
//		}

	}

	public static void NewClass(){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
//		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		frame.add(panel);

		c.anchor = GridBagConstraints.NORTHWEST;
		c.weighty = 1;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new Button("Hello"), c);
		c.gridy = 1;
		panel.add(new Button("Hello"), c);
		c.gridy = 2;
		panel.add(new Button("Hello"), c);
		c.gridy = 0;
		c.gridx = 1;
		panel.add(new Button("Hello"), c);

		frame.pack();
		frame.setSize(800,400);
		frame.setVisible(true);
	}

}
