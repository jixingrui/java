package azura.banshee.chessboard.mask;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import azura.banshee.chessboard.tif.ImageReader;

import common.algorithm.FastMath;
import common.collections.RectanglePlus;
import common.collections.bitset.lbs.LBSet;
import common.graphics.ImageUtil;

@SuppressWarnings("serial")
public class MaskGem extends RectanglePlus {
	public static Logger log = Logger.getLogger(MaskGem.class);

	/**
	 * inclusive
	 */
	private static final int scanRange = 512;
	private ImageReader reader;
	private int color;
	private LBSet lbsColor = new LBSet();

	private MaskGem() {
	}

	private MaskGem(int xLeft, int yDown, ImageReader reader, int color) {
		super(xLeft, yDown, 1, 1);
		this.reader = reader;
		this.color = color;
	}

	static MaskGem create(int xLeft, int yDown, ImageReader image, int color) {
		MaskGem gem = new MaskGem(xLeft, yDown, image, color);

		gem.expand();

		createAccurate(gem, image);

		gem.removeOrphan();

		return gem;
	}

	private static void createAccurate(MaskGem gem, ImageReader image) {
		for (int j = gem.getCeiling(); j <= gem.getFloor(); j++)
			for (int i = gem.getLeft(); i <= gem.getRight(); i++) {
				int colorOfPixel = image.getRGB(i, j, image.zMax);
				boolean hit = colorOfPixel == gem.color;
				gem.lbsColor.push(hit);
			}
	}

	public void toShards(Zmask mask, int z) {

		HashMap<ZmaskTile, Shard> t_s = new HashMap<ZmaskTile, Shard>();

		for (int pos : lbsColor) {
			int xPix = pos % width + x;
			int yPix = pos / width + y;
			ZmaskTile tile = mask.getTile(xPix, yPix, z);
			if (tile == null) {
				log.warn("no tile contains this pixel:(" + xPix + "," + yPix
						+ ") z=" + z);
				continue;
			}

			Shard s = t_s.get(tile);
			if (s == null) {
				s = new Shard(color);
				s.yFoot = this.getFloor();
				t_s.put(tile, s);
			} else {
			}
			s.put(xPix, yPix);
		}

		for (Entry<ZmaskTile, Shard> e : t_s.entrySet()) {
			ZmaskTile tile = e.getKey();
			Shard s = e.getValue();
			s.seal();
			tile.shardList.add(s);
		}
	}

	private void expand() {

		RectanglePlus probed = new RectanglePlus(x, y, width, height);

		boolean leftIn = true;
		boolean rightIn = true;
		boolean topIn = true;
		while (leftIn || rightIn || topIn) {
			leftIn = false;
			rightIn = false;
			topIn = false;
			if (probeLeft(probed)) {
				topIn = true;
			}
			if (probeRight(probed)) {
				topIn = true;
			}
			if (probeTop(probed)) {
				leftIn = true;
				rightIn = true;
			}
		}

	}

	public MaskGem shrink() {
		MaskGem quarter = new MaskGem();
		quarter.color = this.color;
		quarter.x = this.x / 2;
		quarter.y = this.y / 2;
		quarter.width = this.width / 2 + 1;
		quarter.height = this.height / 2 + 1;

		if (quarter.width == 0 || quarter.height == 0)
			return null;

		boolean[][] quarterCanvas = new boolean[quarter.width][quarter.height];

		for (int pos : lbsColor) {
			int xShrink = pos % width / 2;
			int yShrink = pos / width / 2;
			quarterCanvas[xShrink][yShrink] = true;
		}
		for (int j = 0; j < quarter.height; j++)
			for (int i = 0; i < quarter.width; i++) {
				quarter.lbsColor.push(quarterCanvas[i][j]);
			}

		return quarter;
	}

	public MaskGem shrink2() {
		MaskGem quarter = new MaskGem();
		quarter.color = this.color;
		quarter.x = this.x / 2;
		quarter.y = this.y / 2;
		quarter.width = this.width / 2 + 1;
		quarter.height = this.height / 2 + 1;

		if (quarter.width == 0 || quarter.height == 0)
			return null;

		boolean[][] quarterCanvas = new boolean[quarter.width][quarter.height];

		for (int pos : lbsColor) {
			int xO = pos % width;
			int yO = pos / width;
			for (int i = 0; i <= xO % 2; i++)
				for (int j = 0; j <= yO % 2; j++) {
					int xShrink = pos % width / 2 + i;
					int yShrink = pos / width / 2 + j;
					quarterCanvas[xShrink][yShrink] = true;
				}
		}
		for (int j = 0; j < quarter.height; j++)
			for (int i = 0; i < quarter.width; i++) {
				quarter.lbsColor.push(quarterCanvas[i][j]);
			}

		return quarter;
	}

	private void removeOrphan() {
		boolean[][] canvas = new boolean[width][height];
		for (int xyOnGem : lbsColor) {
			int x = xyOnGem % width;
			int y = xyOnGem / width;
			canvas[x][y] = true;
		}

		loop: for (int xyOnGem : lbsColor) {
			int x = xyOnGem % width;
			int y = xyOnGem / width;

			for (int i = x - 1; i <= x + 1; i++) {
				for (int j = y - 1; j <= y + 1; j++) {
					if ((i != x || j != y) && i >= 0 && i < width && j >= 0
							&& j < height) {
						if (canvas[i][j] == true) {
							continue loop;
						}
					}
				}
			}
			canvas[x][y] = false;
		}

		lbsColor.clear();
		for (int j = 0; j < height; j++)
			for (int i = 0; i < width; i++) {
				lbsColor.push(canvas[i][j]);
			}
	}

	private boolean probeLeft(RectanglePlus probed) {
		boolean mod = false;
		int ceiling = probed.getCeiling();
		int floor = probed.getFloor();
		int tantacle = probed.getLeft() - 1;
		while (this.getLeft() - scanRange <= tantacle
				&& reader.bbc.getLeft() <= tantacle) {
			for (int j = ceiling; j <= floor; j++) {
				if (reader.getRGB(tantacle, j, reader.zMax) == color) {
					this.setLeft(tantacle);
					if (j < this.getCeiling()) {
						this.setCeiling(j);
					} else if (this.getFloor() < j) {
						this.setFloor(j);
					}
					mod = true;
				}
			}
			probed.setLeft(tantacle);
			tantacle--;
		}
		return mod;
	}

	private boolean probeRight(RectanglePlus probed) {
		boolean mod = false;
		int ceiling = probed.getCeiling();
		int floor = probed.getFloor();
		int tantacle = probed.getRight() + 1;
		while (tantacle <= this.getRight() + scanRange
				&& tantacle <= reader.bbc.getRight()) {
			for (int j = ceiling; j <= floor; j++) {
				if (reader.getRGB(tantacle, j, reader.zMax) == color) {
					this.setRight(tantacle);
					if (j < this.getCeiling()) {
						this.setCeiling(j);
					} else if (this.getFloor() < j) {
						this.setFloor(j);
					}
					mod = true;
				}
			}
			probed.setRight(tantacle);
			tantacle++;
		}
		return mod;
	}

	private boolean probeTop(RectanglePlus probed) {
		boolean mod = false;
		int left = probed.getLeft();
		int right = probed.getRight();
		int tantacle = probed.getCeiling() - 1;
		while (this.getCeiling() - scanRange <= tantacle
				&& reader.bbc.getCeiling() <= tantacle) {
			for (int i = left; i <= right; i++) {
				if (reader.getRGB(i, tantacle, reader.zMax) == color) {
					this.setCeiling(tantacle);
					if (i < this.getLeft()) {
						this.setLeft(i);
					} else if (this.getRight() < i) {
						this.setRight(i);
					}
					mod = true;
				}
			}
			probed.setCeiling(tantacle);
			tantacle--;
		}
		return mod;
	}

	public void drawFill(BufferedImage image) {
		for (int pos : lbsColor) {
			int xLocal = pos % width;
			int yLocal = pos / width;
			int xt = x + xLocal + image.getWidth() / 2;
			int yt = y + yLocal + image.getHeight() / 2;
			image.setRGB(xt, yt, color);
		}
	}

	public void drawStrip(BufferedImage image) {
		int stripWidth = width;
		while (stripWidth > 100)
			stripWidth = (int) Math.ceil((double) stripWidth / 2);

		int stripCount = (int) Math.ceil((double) width / stripWidth);
		int[] colorList = new int[stripCount];
		for (int i = 0; i < stripCount; i++) {
			colorList[i] = FastMath.random(0xff555555, 0xffffffff);
			// log.info("color=" + Hex.getHex(colorList[i]));
		}

		for (int pos : lbsColor) {
			int xLocal = pos % width;
			int yLocal = pos / width;
			int xt = x + xLocal + image.getWidth() / 2;
			int yt = y + yLocal + image.getHeight() / 2;

			int stripIdx = xLocal / stripWidth;
			int color = colorList[stripIdx];
			image.setRGB(xt, yt, color);
		}
	}

	public void drawBoundingBox(BufferedImage image) {
		Color paintColor = null;
		int xx = x + image.getWidth() / 2;
		int yy = y + image.getHeight() / 2;
		if (width < 4 || height < 4) {
			// log.info("suspicious gem: x="+xx+" y="+yy+" width=" + width +
			// " height=" + height
			// + " color=" + Hex.getHex(color));
			ImageUtil.drawBigDot(image, color, xx, yy);
			paintColor = new Color(color);
			Graphics g = image.getGraphics();
			g.setColor(paintColor);
			g.drawRect(xx, yy, width + 1, height + 1);
			g.dispose();
		} else {
			paintColor = new Color(color & 0x00ffffff | 0x88000000, true);
			Graphics g = image.getGraphics();
			g.setColor(paintColor);
			g.drawRect(xx, yy, width - 1, height - 1);
			g.dispose();
		}
		// if (width < 4 || height < 4) {
		// ImageUtil.drawBigDot(image, color, x, y);
		// } else {
		// }
	}
}
