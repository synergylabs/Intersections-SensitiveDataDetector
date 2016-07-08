package capstone.sdd.core;

import capstone.sdd.gui.GuiListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


public class ScanWorker implements Callable<Void> {
	
	private File folder;
	private Settings settings;
	private ExecutorService pool;
	private GuiListener listener;
	
	public ScanWorker(File folder, ExecutorService pool, GuiListener listener) {
		this.folder = folder;
		this.pool = pool;
		settings = Settings.getInstance();

		this.listener = listener;
	}

	@Override
	public Void call() throws Exception {
		
		File[] files = folder.listFiles();
		List<Callable<Void>> tasks = new ArrayList<>();

		for (File file : files) {
			if (file == null) {
				continue;
			}
				
			if (file.isDirectory()) {
				if (settings.isScan_sub_repo()) {
					pool.submit(new ScanWorker(file, pool, listener));
					tasks.add(new ScanWorker(file, pool, listener));
				}
			} else{
				if (settings.isSupported(file)) {
					listener.addTask(file);
//					pool.submit(new MatchWorker(file, listener)); 	// send the file to match worker to match
				}
			}
		}

		
		return null;
	}
}
