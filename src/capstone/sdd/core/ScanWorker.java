package capstone.sdd.core;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


public class ScanWorker implements Callable<Void> {
	
	private File folder;
	private Settings settings;
	private ExecutorService pool;
	
	public ScanWorker(File folder, ExecutorService pool) {
		this.folder = folder;
		this.pool = pool;
		settings = Settings.getInstance();
	}

	@Override
	public Void call() throws Exception {
		
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file == null) {
				continue;
			}
				
			if (file.isDirectory()) {
				if (settings.isScan_sub_repo()) {
					pool.submit(new ScanWorker(file, pool));
				}
			} else{
				if (settings.isSupported(file)) {
					pool.submit(new MatchWorker(file)); 	// send the file to match worker to match
				}
			}
		}
		
		return null;
	}
}
