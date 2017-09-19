package azura.banshee.pano;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.banshee.util.AtfE;
import azura.gallerid.Swap;

import common.algorithm.FastMath;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class PanoWriterAtf {

	public static void main(String[] args) throws IOException {
//		args = new String[] { "e:/zpano/lift" };
//		File input = new File(args[0]);
//		byte[][][] data = PanoWriterAtf.write6(input);
//		ZintBuffer zb = new ZintBuffer();
//		GalPack gp = new GalPack();
//		for (AtfE os : AtfE.values()) {
//			for (PanoSize size : PanoSize.values()) {
//				String md5 = GalFile
//						.putData(data[os.ordinal()][size.ordinal()]);
//				zb.writeUTFZ(md5);
//				gp.addSlave(md5);
//			}
//		}
//		gp.setMaster(zb.toBytes());
//
//		String outName = input.getParent() + "/" + input.getName() + ".zpano";
//		gp.write(outName);
	}

	/**
	 * @return [pc,android,ios][small,middle,large][data compressed]
	 */
	public static byte[][][] write6(File source) throws IOException {
		// File workFolder = FileUtil.prepareSwapFolder();
		File workFolder = Swap.applyNewSwapSubFolder();

		check(source, "left", workFolder, 0);
		check(source, "right", workFolder, 1);
		check(source, "bottom", workFolder, 2);
		check(source, "top", workFolder, 3);
		check(source, "back", workFolder, 4);
		check(source, "front", workFolder, 5);

		byte[][][] result = new byte[3][3][];

		for (AtfE os : AtfE.values()) {
			byte[][] level2 = new byte[3][];
			result[os.ordinal()] = level2;
			for (PanoSize size : PanoSize.values()) {
				byte[] level1 = write6(workFolder, os, size);
				level1 = FileUtil.compress(level1);
				level2[size.ordinal()] = level1;
			}
		}

		return result;
	}

	private static byte[] write6(File workFolder, AtfE format, PanoSize size) {
		try {

			String inputName = workFolder + "/" + size + "0.png";
			String outputName = workFolder + "/"
					+ FastMath.random(0, Integer.MAX_VALUE) + ".atf";

			String cmd = "./external/png2atf.exe -i " + inputName + " -o "
					+ outputName + " -m -c ";
			switch (format) {
			case pc:
				cmd += "d";
				break;
			case android:
				cmd += "e";
				break;
			case ios:
				cmd += "p";
				break;
			}

			Runtime rn = Runtime.getRuntime();
			Process child = rn.exec(cmd);
			child.waitFor();

			byte[] result = FileUtil.read(outputName);
			new File(inputName).deleteOnExit();
			new File(outputName).deleteOnExit();

			return result;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void check(File folder, String side, File wf, int idx)
			throws IOException {
		File src = new File(folder.getAbsolutePath() + "/" + side + ".png");
		if (!src.exists())
			fail();

		BufferedImage img512 = ImageIO.read(src);

		if (img512.getWidth() != 512 || img512.getHeight() != 512)
			fail();

		// BufferedImage img512 = ImageUtil.scale(img1024, 0.5);
		BufferedImage img256 = ImageUtil.scale(img512, 0.5);
		BufferedImage img128 = ImageUtil.scale(img512, 0.25);

		String s128 = wf + "/" + PanoSize.s128 + idx + ".png";
		FileUtil.write(s128, ImageUtil.encodePng(img128));

		String s256 = wf + "/" + PanoSize.s256 + idx + ".png";
		FileUtil.write(s256, ImageUtil.encodePng(img256));

		String s512 = wf + "/" + PanoSize.s512 + idx + ".png";
		FileUtil.write(s512, ImageUtil.encodePng(img512));

		// String s1024 = wf + "/" + PanoSize.s1024 + idx + ".png";
		// FileUtil.write(s1024, ImageUtil.encodePng(img1024));

		new File(s128).deleteOnExit();
		new File(s256).deleteOnExit();
		new File(s512).deleteOnExit();
		// new File(s1024).deleteOnExit();
	}

	private static void fail() throws IOException {
		System.out.println("top,bottom,left,right,front,back: .png size=512");
		System.in.read();
		System.exit(1);
	}
}
