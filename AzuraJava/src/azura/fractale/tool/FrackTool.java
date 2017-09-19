package azura.fractale.tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import common.algorithm.crypto.HintBook;

public class FrackTool {

	private static ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public static void initLogger(String path) {
		PropertyConfigurator.configure(path);
		Logger.getLogger("io.netty").setLevel(Level.WARN);
		Logger.getLogger("org.apache.http").setLevel(Level.WARN);
	}

	public static void shutdownHook() {
		new Thread(() -> {
			Logger.getLogger(FrackTool.class).debug("Press ENTER to call System.exit() and run the shutdown routine.");
			try {
				System.in.read();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		}).start();
	}

	public static byte[] readCodeBook(String path) {
		return HintBook.readBookFromImage(path);
	}

	public static void run(Runnable connection) {
		pool.execute(connection);
	}

}
