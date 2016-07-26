package capstone.sdd.main;

import capstone.sdd.gui.MainFrame;
import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
	public static void  main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new MainFrame();
		});
	}

}
