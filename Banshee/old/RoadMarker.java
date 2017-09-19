package azura.banshee.zbase.node;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.banshee.chessboard.tif.ImageReader;

import common.util.FileUtil;

public class RoadMarker {

	public static void main(String[] args) throws IOException {
		args = new String[] { "D:\\temp\\island.2250.tif" };

		ImageReader reader = ImageReader.load(args[0], true);

		GeoMap gm = new GeoMap(reader.frameWidth(reader.zMax),
				reader.frameHeight(reader.zMax));

		for (int y = 0; y < reader.frameHeight(reader.zMax); y++) {
			for (int x = 0; x < reader.frameWidth(reader.zMax); x++) {
				int color = reader.getRGB(x, y, reader.zMax);
				boolean isRoad = (color == 0xffffffff);
				if (isRoad) {
					gm.skeletonSet(x, y, true);
				}
			}
		}

		reader.dispose();

		ZhangSuenThinning.thin(gm.skeleton, gm.height, gm.width);
		NodeClassifier.classify(gm);

		// BufferedImage ske = gm.print();
		// ImageIO.write(ske, "png", new File("d:\\temp\\island.2250.ske.png"));

		NodeMap nm = new NodeMap();
		// nm.load(gm);

		byte[] raw = nm.toBytes();
		byte[] zip = FileUtil.compress(raw);
		System.out.println(raw.length + " -> " + zip.length);

		// BufferedImage bi = gm.print();
		BufferedImage bi = new BufferedImage(gm.width, gm.height,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage way = nm.draw(bi);
		ImageIO.write(way, "png", new File("d:\\temp\\island.2250.way.png"));

	}
}
