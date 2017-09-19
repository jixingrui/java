package azura.gallerid;

import java.io.File;

import org.apache.log4j.Logger;

import common.algorithm.FastMath;

public class Swap {
	static Logger log = Logger.getLogger(Swap.class);

	private static File swapRoot;

	public static void setLocation(File input) {
		if (swapRoot != null)
			throw new Error("Swap: root locked");

		File ground = input.getParentFile();
		makeRoot(ground);

		log.info("swap folder set to " + ground.getAbsolutePath());
	}

	public static File applyNewSwapSubFolder() {

		File swapFolder = new File(getRoot().getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + "/");
		if (!swapFolder.exists()) {
			swapFolder.mkdir();
		}

		swapFolder.deleteOnExit();

		return swapFolder;
	}

	private static File getRoot() {
		if (swapRoot != null)
			return swapRoot;

		File ground = new File(System.getProperty("java.io.tmpdir"));
		makeRoot(ground);

		log.info("swap folder not set. default to " + ground.getAbsolutePath());

		return swapRoot;
	}

	private static void makeRoot(File ground) {
		if (!ground.exists() || !ground.canWrite())
			throw new Error("Swap: cannot create swap folder");

		swapRoot = new File(ground.getPath() + "/swap");
		if (!swapRoot.exists()) {
			swapRoot.mkdir();
		} else if (!swapRoot.isDirectory()) {
			swapRoot.delete();
			swapRoot.mkdir();
		}

		swapRoot.deleteOnExit();
	}
}
