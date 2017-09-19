package azura.banshee.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.gallerid.Swap;

import common.algorithm.FastMath;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class AtfUtilOld {
	private static final File folder = Swap.applyNewSwapSubFolder();

	// public static byte[] image2atf8888(BufferedImage bitmap) {
	// String inputName = tempFolder + "/"
	// + FastMath.random(0, Integer.MAX_VALUE) + ".png";
	// String outputName = tempFolder + "/"
	// + FastMath.random(0, Integer.MAX_VALUE) + ".atf";
	// try {
	// ImageIO.write(bitmap, "png", new File(inputName));
	//
	// String cmd = "./external/png2atf.exe -i " + inputName + " -o "
	// + outputName;
	//
	// Runtime rn = Runtime.getRuntime();
	// Process child = rn.exec(cmd);
	// child.waitFor();
	//
	// byte[] result = FileUtil.read(outputName);
	// new File(inputName).deleteOnExit();
	// new File(outputName).deleteOnExit();
	//
	// return result;
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	/**
	 * @return [pc,android,ios], compressed
	 */
	public static byte[][] image2atf(BufferedImage bitmap) {
		byte[][] result = new byte[3][];

		byte[] pc = image2atf(bitmap, AtfE.pc);
		pc = FileUtil.compress(pc);
		result[0] = pc;

		byte[] android = image2atf(bitmap, AtfE.android);
		android = FileUtil.compress(android);
		result[1] = android;

		byte[] ios = image2atf(bitmap, AtfE.ios);
		ios = FileUtil.compress(ios);
		result[2] = ios;

		return result;
	}

	private static byte[] image2atf(BufferedImage bitmap, AtfE format) {
		String inputName = folder.getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + ".png";
		String outputName = folder.getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + ".atf";
		try {
			ImageIO.write(bitmap, "png", new File(inputName));

			String cmd = "./external/png2atf_0.8.exe -i " + inputName + " -o "
					+ outputName +" -n 0,0 -q 0 ";
			switch (format) {
			case pc:
				cmd += "-c d";
				break;
			case android:
				cmd += "-c e";
				break;
			case ios:
				cmd += "-c e2";
				break;
			}

			Runtime rn = Runtime.getRuntime();
			Process child = rn.exec(cmd);
			child.waitFor();

			byte[] result = FileUtil.read(outputName);
			new File(inputName).delete();
			new File(outputName).delete();

			return result;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage imageToN2(BufferedImage source) {
		int w2 = FastMath.getNextPowerOfTwo(source.getWidth());
		int h2 = FastMath.getNextPowerOfTwo(source.getHeight());

		if (w2 > 2048 || h2 > 2048)
			throw new IllegalArgumentException("image must not exceed 2048");
		if (w2 == source.getWidth() && h2 == source.getHeight())
			return source;

		BufferedImage out = new BufferedImage(w2, h2,
				BufferedImage.TYPE_INT_ARGB);
		ImageUtil.copyPixels(source, out, w2 / 2 - source.getWidth() / 2, h2
				/ 2 - source.getHeight() / 2);

		return out;
	}
}
