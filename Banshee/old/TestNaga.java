package azura.banshee.nagaOld;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import common.algorithm.FastMath;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class TestNaga {

	private static Random random = new Random();

	public static void main(String[] args) throws IOException {
		String folder = "./input/test/";
		FileUtil.prepareFolder(folder);
		int count = 9;

		File[] array = new File[count];
		File[][] matrix = new File[1][];
		matrix[0] = array;

		for (int i = 0; i < count; i++) {
			int color = getRandomColor();
			int width = getRandomWidth();
			int height = getRandomHeight();
			BufferedImage box = createBox(width, height, color);
			byte[] data = ImageUtil.encodePng(box);
			String fileName = folder + i + ".png";
			FileUtil.write(fileName, data);
			array[i] = new File(fileName);
		}

		// Naga14Writer naga = new Naga14Writer(matrix, 30);
		// byte[] data = naga.encode();
		//
		// naga.write("./input/test_out/");
	}

	private static int getRandomHeight() {
		return FastMath.random(1, 600);
	}

	private static int getRandomWidth() {
		return FastMath.random(1, 600);
	}

	private static int getRandomColor() {
		int color = random.nextInt() | 0xff000000;
		return color;
	}

	private static BufferedImage createBox(int width, int height, int color) {
		BufferedImage result = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();
		g.setColor(new Color(color));
		g.fillRect(0, 0, width, height);
		g.dispose();
		return result;
	}

}
