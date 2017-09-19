package azura.banshee.zebra2;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import azura.banshee.main.ZebraFrame;
import azura.banshee.util.FileMatrix2;
import azura.gallerid.Swap;

import common.algorithm.FastMath;

public class Zebra2Creator {

	public static final Logger log = Logger.getLogger(Zebra2Creator.class);

	public static void main(String[] args) {

		PropertyConfigurator.configure("log4j.properties");
		Logger.getLogger("io.netty").setLevel(Level.WARN);

		args = new String[] { "Z:/temp/zebra/car.png" };
		File input = new File(args[0]);

		Swap.setLocation(input);

		long start = System.currentTimeMillis();
		process(input);
		log.info("time elapsed " + FastMath.timeSpent(start));
	}

	private static void createZimage(File input) {
		String output = input.getPath() + ".zebra";
		if (new File(output).exists())
			log.info("skip !! " + output);
		else {
			log.info("creating " + output);
			Zebra2 zebra = new Zebra2();
			zebra.loadZimage(input);

			zebra.packTo(output);

			// GalPack gp = new GalPack();
			// gp.setMaster(zebra.toBytes());
			// zebra.extractMe5To(gp);
			// gp.writeTo(output);
			//
			// gp.cleanUp();
			// zebra.atlas.cleanUp();
		}
	}

	private static void createZmatrix(FileMatrix2 fm, String output) {
		if (new File(output).exists())
			log.info("skip !! " + output);
		else {
			log.info("creating " + output);
			Zebra2 zebra = new Zebra2();
			zebra.loadZmatrix(fm);

			zebra.packTo(output);

		}
	}

	public static void process(File input) {

		if (!input.exists()) {
			log.error("input not found: " + input.getPath());
		}

		if (input.isDirectory()) {
			if (isSwap(input)) {
				// log.info("swap found " + input.getPath());
				return;
			}

			FileMatrix2 fm = FileMatrix2.assemble(input);
			if (fm != null) {
				// log.info("zmatrix !! " + input.getPath());
				String output = input.getPath() + ".zebra";
				createZmatrix(fm, output);
			} else {
				Arrays.stream(input.listFiles()).forEach(sub -> {
					process(sub);
				});
			}
		} else {
			if (isZimage(input)) {
				// log.info("zimage !! " + input.getPath());
				// String output = input.getPath() + ".zebra";
				createZimage(input);
			} else {
				// log.info("nothing found " + input.getPath());
			}
		}
	}

	private static boolean isZimage(File input) {
		String[] chunk = input.getName().toLowerCase().split("[\\.]");
		if (chunk.length != 2)
			return false;

		switch (chunk[1]) {
		case "png":
		case "tif":
		case "jpg":
			return true;
		default:
			return false;
		}
	}

	private static boolean isSwap(File input) {
		return input.getName().equals("swap");
	}

	public static ZebraFrame getZebraFrame(File input) {

		String fileName = input.getName();

		String[] chunk = fileName.split("[_\\.]");
		if (chunk == null || chunk.length != 3)
			return null;

		ZebraFrame frame = new ZebraFrame();
		try {
			frame.row = Integer.parseInt(chunk[0]);
			frame.frame = Integer.parseInt(chunk[1]);
			frame.file = input;
			return frame;
		} catch (Exception e) {
			return null;
		}
	}

}
