package azura.banshee.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import common.util.FileUtil;

public class Konggui {

	public static void toMaxtrix(String input) throws IOException {
		String outFolder = FileUtil.getNoExt(input);
		FileUtil.prepareFolder(outFolder);
		BufferedImage image = ImageIO.read(new File(input));
		if (valid(image)) {
			int frameCount = image.getHeight() / 256 / 8;
			for (int row = 0; row < 8; row++)
				for (int frame = 0; frame < frameCount; frame++) {
					BufferedImage single = new BufferedImage(256, 340,
							BufferedImage.TYPE_INT_ARGB);

					BufferedImage sub = image.getSubimage(0,
							((row + 2) % 8 + frame * 8) * 256, 256, 256);
					single.getGraphics().drawImage(sub, 0, 0, null);

					File frameFile = new File(outFolder + "\\" + row + "_"
							+ frame + ".png");
					ImageIO.write(single, "png", frameFile);
				}
		}
	}

	private static boolean valid(BufferedImage img) {
		return img != null && img.getWidth() == 256 && img.getHeight() > 0
				&& img.getHeight() % 256 == 0;
	}

	public static void main(String[] args) throws IOException {
		Konggui.toMaxtrix("./input/konggui.png");
	}
}
