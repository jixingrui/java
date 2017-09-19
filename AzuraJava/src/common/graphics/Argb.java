package common.graphics;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;

public class Argb {
	private static final int CheckSize = 10000;
	private static final int ModePng = 1;
	private static final int ModeAjpg = 2;

	private int mode = ModeAjpg;
	private int width, height;
	private Alpha5 alpha;
	private byte[] jpg;
	private byte[] png;

//	public static void main(String[] args) throws IOException,
//			InterruptedException {
//
//		if (args.length != 1) {
//			System.out.println("this.jar file.png");
//			return;
//		}
//
//		String fileName = args[0];
//		File file = new File(fileName);
//		if (!file.exists()) {
//			return;
//		}
//
//		BufferedImage png = ImageIO.read(file);
//		Argb argb = new Argb(png);
//
//		System.out.println("jpg=" + argb.jpg.length + " alpha="
//				+ argb.alpha.toBytes().length);
//
//		FileUtil.write(FileUtil.getNoExt(fileName) + ".argb",
//				argb.toBytes());
//		ImageIO.write(argb.getImage(), "png", new File("ajpg_out.png"));
//	}

	public static byte[] image2bytes(BufferedImage image) {
		return new Argb(image).toBytes();
	}

	public static BufferedImage bytes2image(byte[] data) {
		return new Argb(data).getImage();
	}

	private Argb(BufferedImage argb) {
		this(argb, 0.86f);
	}

	private Argb(BufferedImage argb, float quality) {
		width = argb.getWidth();
		height = argb.getHeight();
		alpha = new Alpha5();
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				alpha.put(argb.getRGB(i, j));
			}
		jpg = ImageUtil.encodeJpg(argb, quality);

		int sizeAjpg = jpg.length + alpha.toBytes().length;
		if (sizeAjpg < CheckSize) {
			png = ImageUtil.encodePng(argb);
			if (png.length < jpg.length + alpha.toBytes().length) {
				mode = ModePng;
			}
		}
	}

	public BufferedImage getImage() {
		BufferedImage argbImage = null;
		if (mode == ModePng) {
			try {
				return ImageIO.read(new ByteArrayInputStream(png));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (mode == ModeAjpg) {
			BufferedImage rgbImage = null;
			try {
				rgbImage = ImageIO.read(new ByteArrayInputStream(jpg));
			} catch (IOException e) {
				e.printStackTrace();
			}
			argbImage = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			argbImage.getGraphics().drawImage(rgbImage, 0, 0, null);
			alpha.pasteTo(argbImage);
		}
		return argbImage;
	}

	private Argb(byte[] data) {
		ZintReaderI zb = new ZintBuffer(data);
		mode = zb.readZint();
		width=zb.readZint();
		height=zb.readZint();
		if (mode == ModeAjpg) {
			alpha = new Alpha5(zb.readBytesZ());
			jpg = zb.readBytesZ();
		} else if (mode == ModePng) {
			png = zb.readBytesZ();
		}
	}

	private byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(mode);
		zb.writeZint(width);
		zb.writeZint(height);
		if (mode == ModeAjpg) {
			// Trace.trace("ajpg");
			zb.writeBytesZ(alpha.toBytes());
			zb.writeBytesZ(jpg);
		} else if (mode == ModePng) {
			// Trace.trace("png");
			zb.writeBytesZ(png);
		}
		return zb.toBytes();
	}

}
