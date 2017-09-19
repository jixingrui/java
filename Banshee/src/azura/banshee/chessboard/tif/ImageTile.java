package azura.banshee.chessboard.tif;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

import javax.media.jai.PlanarImage;

import common.graphics.ImageUtil;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.fi.TileFi;

public class ImageTile extends TileFi<ImageTile> {

	SoftReference<BufferedImage> holder = new SoftReference<BufferedImage>(null);

	public ImageTile(int fi, PyramidFi<ImageTile> pyramid) {
		super(fi, pyramid);
	}

	public BufferedImage getImage() {
		BufferedImage bi = holder.get();
		if (bi == null) {
			if (z == 0) {
				bi = getPyramid().root;
			} else {
				bi = loadTileImage(getPyramid().sourceArray[z].image);
			}

			holder = new SoftReference<BufferedImage>(bi);

			// Debug.draw(bi, "z:/map/debug/" + z + "_" + fi + ".png");
		}
		return bi;
	}

	private BufferedImage loadTileImage(PlanarImage sourceImage) {
		BufferedImage bi = null;
		int tileSide = getPyramid().tileSide;
		int size = sideLength() * tileSide;
		Rectangle sourceRect = new Rectangle(
				(size - sourceImage.getWidth()) / 2,
				(size - sourceImage.getHeight()) / 2, sourceImage.getWidth(),
				sourceImage.getHeight());
		Rectangle tileRect = new Rectangle(xp * tileSide, yp * tileSide,
				tileSide, tileSide);

		if (sourceRect.contains(tileRect)) {
			tileRect.x -= sourceRect.x;
			tileRect.y -= sourceRect.y;
			bi = sourceImage.getAsBufferedImage(tileRect, null);
		} else if (sourceRect.intersects(tileRect)) {

			int dx = 0, dy = 0;
			if (tileRect.x < sourceRect.x)
				dx = sourceRect.x % tileSide;
			if (tileRect.y < sourceRect.y)
				dy = sourceRect.y % tileSide;

			tileRect.x -= sourceRect.x;
			tileRect.y -= sourceRect.y;
			BufferedImage temp = sourceImage.getAsBufferedImage(tileRect, null);
			bi = ImageUtil.newImage(tileSide, tileSide, getBgColor());
			Graphics g = bi.createGraphics();
			g.drawImage(temp, dx, dy, null);
			g.dispose();
		} else {
			bi = ImageUtil.newImage(tileSide, tileSide, getBgColor());
		}
		return bi;
	}

	private Color getBgColor() {
		ImageReader host = (ImageReader) pyramid;
		return host.bgColor;
	}

	public void dispose() {
		holder.clear();
		holder = null;
	}

	private ImageReader getPyramid() {
		return (ImageReader) pyramid;
	}

	public int getRGB(int x, int y) {
		BufferedImage bi = getImage();
		return bi.getRGB(x, y);
	}

	public void sleep() {
		holder.clear();
	}
}
