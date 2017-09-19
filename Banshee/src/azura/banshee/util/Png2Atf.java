package azura.banshee.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.gallerid.Swap;

import common.algorithm.FastMath;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class Png2Atf {
	private static final File folder = Swap.applyNewSwapSubFolder();

	public static byte[] convert_888(BufferedImage bitmap) throws IOException,
			InterruptedException {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width != height)
			throw new Error();
		if (FastMath.isPowerOfTwo(width) == false)
			throw new Error();

		String pngPath = folder.getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + ".png";
		ImageIO.write(bitmap, "png", new File(pngPath));

		byte[] atf = null;

		if (width == 2048) {
			atf = png2atf(pngPath, "-r -q 10");
		} else {
			atf = png2pvr2atf(pngPath, "-f r8g8b8a8,UBN,lRGB", "-q 10 -r");
		}

		atf = FileUtil.compress(atf);

		new File(pngPath).delete();

		return atf;
	}

	/**
	 * @return [pc,android,ios], compressed
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static byte[][] convert_dxt_etc_pvr(BufferedImage bitmap)
			throws IOException, InterruptedException {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		if (width != height)
			throw new Error();
		if (FastMath.isPowerOfTwo(width) == false)
			throw new Error();

		String pngPath = folder.getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + ".png";
		ImageIO.write(bitmap, "png", new File(pngPath));

		byte[][] result = new byte[3][];

		if (width == 2048) {
			result[0] = png2atf(pngPath, "-q 0 -c d");
			result[1] = png2atf(pngPath, "-q 0 -c e");
			result[2] = png2atf(pngPath, "-q 0 -c e2");
		} else {
			result[0] = png2pvr2atf(pngPath, "-f bc3,UBN,lRGB", "-d");
			result[1] = png2atf(pngPath, "-q 0 -c e");
			result[2] = png2atf(pngPath, "-q 0 -c e2");
			// result[2] = png2pvr2atf(pngPath, "-f r8g8b8a8,UBN,lRGB", "-r");
			// result[2] = png2pvr2atf(pngPath,
			// "-f PVRTC1_4,UBN,lRGB -q pvrtcbest", "-p");
		}

		result[0] = FileUtil.compress(result[0]);
		result[1] = FileUtil.compress(result[1]);
		result[2] = FileUtil.compress(result[2]);

		new File(pngPath).delete();

		return result;
	}

	private static byte[] png2pvr2atf(String pngPath, String pvrFormat,
			String atfFormat) throws IOException, InterruptedException {

		String pvrPath = folder.getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + ".pvr";

		String atfPath = folder.getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + ".atf";

		String png2pvr = "./external/PVRTexToolCLI_4.16.exe -i " + pngPath
				+ " -o " + pvrPath + " -l -m 1 -dither " + pvrFormat;
		Runtime.getRuntime().exec(png2pvr).waitFor();

		String pvr2atf = "./external/pvr2atf_0.5.exe -n 0,0 " + atfFormat
				+ " " + pvrPath + " -o " + atfPath;
		Runtime.getRuntime().exec(pvr2atf).waitFor();

		byte[] result = FileUtil.read(atfPath);
		new File(pvrPath).delete();
		new File(atfPath).delete();

		return result;
	}

	private static byte[] png2atf(String pngPath, String atfFormat)
			throws IOException, InterruptedException {
		String atfPath = folder.getPath() + "/"
				+ FastMath.random(0, Integer.MAX_VALUE) + ".atf";

		String cmd = "./external/png2atf_0.8.exe -i " + pngPath + " -o "
				+ atfPath + " -n 0,0 " + atfFormat;
		Runtime rn = Runtime.getRuntime();
		Process child = rn.exec(cmd);
		child.waitFor();

		byte[] result = FileUtil.read(atfPath);
		new File(atfPath).delete();

		return result;
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

// private static byte[] image2atf(BufferedImage bitmap, AtfE format) {
// String pngPath = folder.getPath() + "/"
// + FastMath.random(0, Integer.MAX_VALUE) + ".png";
// String atfPath = folder.getPath() + "/"
// + FastMath.random(0, Integer.MAX_VALUE) + ".atf";
//
// System.out.println(format.name() + " " + atfPath);
//
// try {
// ImageIO.write(bitmap, "png", new File(pngPath));
//
// switch (format) {
// case pc: {
// String pvrPath = folder.getPath() + "/"
// + FastMath.random(0, Integer.MAX_VALUE) + ".pvr";
//
// String png2pvr =
// "./external/PVRTexToolCLI_4.16.exe -f bc3,UBN,lRGB -l -m 1 -dither -i "
// + pngPath + " -o " + pvrPath;
// Runtime.getRuntime().exec(png2pvr).waitFor();
//
// String pvr2atf = "./external/pvr2atf_0.5.exe -n 0,0 -q 0 -d "
// + pvrPath + " -o " + atfPath;
// Runtime.getRuntime().exec(pvr2atf).waitFor();
//
// new File(pvrPath).delete();
// }
// break;
// case android: {
// String cmd = "./external/png2atf_0.8.exe -i " + pngPath
// + " -o " + atfPath + " -c e -n 0,0 -q 0";
// Runtime rn = Runtime.getRuntime();
// Process child = rn.exec(cmd);
// child.waitFor();
// }
// break;
// case ios: {
// String pvrPath = folder.getPath() + "/"
// + FastMath.random(0, Integer.MAX_VALUE) + ".pvr";
//
// String png2pvr =
// "./external/PVRTexToolCLI_4.16.exe -f PVRTC1_4,UBN,lRGB -q pvrtcbest -l -m 1 -dither -i "
// + pngPath + " -o " + pvrPath;
// Runtime.getRuntime().exec(png2pvr).waitFor();
//
// String pvr2atf = "./external/pvr2atf_0.5.exe -n 0,0 -q 0 -p "
// + pvrPath + " -o " + atfPath;
// Runtime.getRuntime().exec(pvr2atf).waitFor();
//
// new File(pvrPath).delete();
// }
// break;
// }
//
// if (new File(atfPath).exists() == false)
// throw new Error();
//
// System.out.println("atf size=" + FileUtil.read(atfPath).length);
//
// byte[] result = FileUtil.read(atfPath);
// new File(pngPath).delete();
// new File(atfPath).delete();
//
// return result;
// } catch (IOException e) {
// e.printStackTrace();
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// return null;
// }