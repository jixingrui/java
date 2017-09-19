package azura.banshee.pano;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import common.graphics.ImageUtil;

public class PanoWriterJpg {

	// public static void main(String[] args) throws IOException {
	// args = new String[] { "e:/zpano/1" };
	// File input = new File(args[0]);
	//
	// Swap.setSwapDrive(input);
	//
	// byte[][][] data = write18(input);
	// ZintBuffer zb = new ZintBuffer();
	// GalPack gp = new GalPack();
	// for (int scale = 0; scale <= 2; scale++) {
	// ZintBuffer level = new ZintBuffer();
	// for (int dir = 0; dir < 6; dir++) {
	// level.writeBytes(data[dir][scale]);
	// }
	// String md5 = GalFile.putData(level.toBytes());
	// zb.writeUTFZ(md5);
	// gp.addSlave(md5);
	// }
	// // byte[] zip = FileUtil.compress(zb.toBytes());
	// // gp.setMaster(zip);
	// gp.setMaster(zb.toBytes());
	//
	// String outName = input.getParent() + "/" + input.getName() + ".zpano";
	// gp.write(outName);
	// }

	/**
	 * @return [6][3][]
	 * @throws IOException
	 */
	public static byte[][][] write18(File folder) throws IOException {

		byte[][][] result = new byte[6][][];
		result[0] = write(folder, "right");
		result[1] = write(folder, "left");
		result[2] = write(folder, "top");
		result[3] = write(folder, "bottom");
		result[4] = write(folder, "front");
		result[5] = write(folder, "back");

		return result;
	}

	/**
	 * @return [3][]
	 * @throws IOException
	 */
	private static byte[][] write(File folder, String string)
			throws IOException {
		byte[][] result = new byte[3][];
		File face = new File(folder.getAbsolutePath() + "/" + string + ".png");
		if (!face.exists()) {
			face = new File(folder.getAbsolutePath() + "/" + string + ".jpg");
			if (!face.exists())
				fail();
		}
		BufferedImage img2 = ImageIO.read(face);
		if (img2 == null || img2.getWidth() != 1024 || img2.getHeight() != 1024)
			fail();

		BufferedImage img1 = ImageUtil.scale(img2, 0.5);
		BufferedImage img0 = ImageUtil.scale(img2, 0.25);

		result[0] = ImageUtil.encodeJpg(img0, 0.9f);
		result[1] = ImageUtil.encodeJpg(img1, 0.9f);
		result[2] = ImageUtil.encodeJpg(img2, 0.9f);
		return result;
	}

	private static void fail() throws IOException {
		System.out.println("top,bottom,left,right,front,back: .png/.jpg width=height=1024");
		System.in.read();
		System.exit(1);
	}
}
