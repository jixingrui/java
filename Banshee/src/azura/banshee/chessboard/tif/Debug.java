package azura.banshee.chessboard.tif;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Debug {

	public static void draw(BufferedImage im, String output) {
		try {
			ImageIO.write(im, "png", new File(output));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
