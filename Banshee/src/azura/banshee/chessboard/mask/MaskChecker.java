package azura.banshee.chessboard.mask;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.banshee.chessboard.tif.ImageReader;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class MaskChecker {

	public static void check(File input) throws IOException {

		ImageReader reader = ImageReader.load(input.getPath(), false);
		Zmask mask = new Zmask();

		mask.create(reader);
		reader.dispose();

		BufferedImage output = ImageUtil
				.newImage(reader.frameWidth(reader.zMax), reader
						.frameHeight(reader.zMax), new Color(0xff000000,
						true));

		for (MaskGem gem : mask.color_MaskGem.values()) {
			gem.drawFill(output);
		}
		for (MaskGem gem : mask.color_MaskGem.values()) {
			gem.drawBoundingBox(output);
		}

		ImageIO.write(output, "png",
				new File(FileUtil.getNoExt(input.getPath()) + "_check.png"));

	}
}
