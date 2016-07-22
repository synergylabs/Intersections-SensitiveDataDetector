package capstone.sdd.main;

import capstone.sdd.gui.MainFrame;
import javax.swing.*;
import java.io.File;


public class Main {
	public static void  main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new MainFrame();
		});
	}

}
