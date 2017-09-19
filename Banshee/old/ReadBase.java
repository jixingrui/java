package old;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.banshee.chessboard.base.PyramidBase;

import common.algorithm.FoldIndex;
import common.util.FileUtil;

public class ReadBase {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		byte[] data = FileUtil.read(new File("base.base"));
		data = FileUtil.uncompress(data);

		PyramidBase pb = new PyramidBase();
		pb.load(data);

		int side = FoldIndex.getBound(pb.levelMax) * 256;
		BufferedImage bi = new BufferedImage(side, side,
				BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < side; i++)
			for (int j = 0; j < side; j++) {
//				if (pb.getLand(i, j)) {
//					bi.setRGB(i, j, BaseColor.green);
//				}
			}

		ImageIO.write(bi, "png", new File("contour_exp.png"));
	}

}
