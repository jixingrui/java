package azura.banshee.zebra2;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Zebra2Extractor {
	public static final Logger log = Logger.getLogger(Zebra2Creator.class);

	public static void main(String[] args) throws IOException {

		PropertyConfigurator.configure("log4j.properties");
		Logger.getLogger("io.netty").setLevel(Level.WARN);

		args = new String[] { "Z:/temp/zebra" };
		File input = new File(args[0]);

		if (!input.exists() || !input.isDirectory()) {
			log.error("invalid directory: " + input.getPath());
			return;
		}
		System.out.println("=========================================");
		System.out.println("Are you sure to delete every .zebra under "
				+ input.getPath() + " ? [y/n]");
		System.out.println("=========================================");
		// char answer = (char) System.in.read();
		char answer = 'y';
		if (answer == 'y') {
			clear(input);
			log.info("all .zebra deleted");
		} else {
			log.info("task aborted");
		}
	}

	private static void clear(File input) {
		if (input.isFile()) {
			if (input.getName().endsWith(".zebra")) {
				input.delete();
				log.info("deleted " + input.getPath());
			}
		} else {
			for (File child : input.listFiles()) {
				clear(child);
			}
		}
	}

}
