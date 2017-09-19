package azura.banshee.nagaOld;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.ZintBuffer;
import common.graphics.ImageUtil;

public class Frame implements Comparable<Frame> {
	int xOnSheet, yOnSheet;
	int width, height;
	int xLeftTop, yLeftTop;
	int xCenter, yCenter;
	int onSheet;
	/**
	 * relative to shrinked image
	 */
	private LBSet lbs = new LBSet();

	BufferedImage shrinkedImage;
	private AtfAtlas atlas;

	public Frame(AtfAtlas atlas, BufferedImage source) {
		this.atlas = atlas;

		Rectangle shrink = ImageUtil.getBoundingBox(source);
		// shrink.x = Math.max(0, shrink.x - 1);
		// shrink.y = Math.max(0, shrink.y - 1);
		// shrink.width = Math.min(source.getWidth(), shrink.width + 1);
		// shrink.height = Math.min(source.getHeight(), shrink.height + 1);
		if (shrink.width == 0 || shrink.height == 0) {
			shrink = new Rectangle(0, 0, 4, 4);
			shrinkedImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		} else {
			shrinkedImage = source.getSubimage(shrink.x, shrink.y,
					shrink.width, shrink.height);
		}

		width = shrink.width;
		height = shrink.height;

		xLeftTop = shrink.x;
		yLeftTop = shrink.y;
		xCenter = source.getWidth() / 2 - shrink.x;
		yCenter = source.getHeight() / 2 - shrink.y;

		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++) {
				int color = shrinkedImage.getRGB(i, j);
				if (color >>> 24 != 0)
					lbs.push(true);
				else
					lbs.push(false);
			}
	}

	// public void write(BufferedImage sheet, BufferedImage dest, Shard shard) {
	// for (int i : lbs) {
	// int dx = i % width;
	// int dy = i / width;
	// int color = sheet.getRGB(xOnSheet + dx, yOnSheet + dy);
	// dest.setRGB((shard.x + this.xLeftTop + dx) % 256, (shard.y
	// + this.yLeftTop + dy) % 256, color);
	// }
	// }

	public byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(xOnSheet);
		zb.writeZint(yOnSheet);
		zb.writeZint(width);
		zb.writeZint(height);
		zb.writeZint(xLeftTop);
		zb.writeZint(yLeftTop);
		zb.writeZint(xCenter);
		zb.writeZint(yCenter);
		zb.writeUTFZ(atlas.sheetList.get(onSheet).md5[0]);
		zb.writeUTFZ(atlas.sheetList.get(onSheet).md5[1]);
		zb.writeUTFZ(atlas.sheetList.get(onSheet).md5[2]);
		zb.writeBytes(lbs.toBytes());
		return zb.toBytes();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("xOnSheet=" + xOnSheet + ",");
		sb.append("yOnSheet=" + yOnSheet + ",");
		sb.append("width=" + width + ",");
		sb.append("height=" + height + ",");
		sb.append("xLeftTop=" + xLeftTop + ",");
		sb.append("yLeftTop=" + yLeftTop + ",");
		sb.append("xCenter=" + xCenter + ",");
		sb.append("yCenter=" + yCenter + ",");
		sb.append("onSheet=" + onSheet + "\n");
		return sb.toString();
	}

	@Override
	public int compareTo(Frame other) {
		if (this.equals(other))
			return 0;
		else if (this.width + this.height > other.width + other.height)
			return -1;
		else
			return 1;
	}

}
