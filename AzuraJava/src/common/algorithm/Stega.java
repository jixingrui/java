package common.algorithm;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

public class Stega {

	public static BufferedImage encode(BufferedImage paper, byte[] data) {
		int width = paper.getWidth();
		int height = paper.getHeight();

		if (data.length > width * height)
			throw new IllegalArgumentException(
					"size not match: pixel must >= byte length");
		else if (data.length < width * height) {
			byte[] tmp = new byte[width * height];
			Random rand = new Random();
			rand.nextBytes(tmp);
			System.arraycopy(data, 0, tmp, 0, data.length);
			data = tmp;
		}

		BufferedImage work = toRgb(paper);
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				int color = work.getRGB(i, j);
				color = embed233(color, data[i * height + j]);
				work.setRGB(i, j, color);
			}
		return work;
	}

	private static int embed233(int color, byte e) {
		color &= 0xfffcf8f8;
		color |= (e & 0xc0) << 10;
		color |= (e & 0x38) << 5;
		color |= e & 0x7;
		return color;
	}

	public static byte[] decode(BufferedImage book, int length) {
		int width = book.getWidth();
		int height = book.getHeight();
		
		if (length > width * height)
			throw new IllegalArgumentException(
					"size not match: pixel must >= byte length");

		BufferedImage sg = toRgb(book);
		byte[] extracted = new byte[width * height];
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				int color = sg.getRGB(i, j);
				extracted[i * height + j] = extract233(color);
			}
		byte[] result = Arrays.copyOf(extracted, length);
		return result;
	}

	private static byte extract233(int color) {
		byte b = 0x0;
		b |= color & 0x7;
		b |= (color & 0x700) >>> 5;
		b |= (color & 0x30000) >>> 10;
		return b;
	}

	private static BufferedImage toRgb(BufferedImage source) {
		BufferedImage editable = new BufferedImage(source.getWidth(),
				source.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = editable.createGraphics();
		graphics.drawRenderedImage(source, null);
		graphics.dispose();
		return editable;
	}

//	public static void main(String[] args) throws IOException {
//		BufferedImage bi = ImageIO.read(new File("tomoe120x120.png"));
//
//		Random random = new Random();
//		byte[] book = new byte[(4 + 32) * 360];
//		random.nextBytes(book);
//
//		BufferedImage sgImage = Stega.encode(bi, book);
//
//		ImageIO.write(sgImage, "png", new File("sgOut.png"));
//
//		byte[] extracted = Stega.decode(sgImage,120*120);
//
//		Trace.trace(Arrays.equals(book, extracted));
//	}
}
