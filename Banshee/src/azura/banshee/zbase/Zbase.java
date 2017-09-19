package azura.banshee.zbase;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.tif.Debug;
import azura.banshee.zebra2.tif2.ImageReader2;
import azura.banshee.zebra2.tif2.ImageReaderTile2;
import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class Zbase extends PyramidFi<ZbaseTile> implements BytesI {

	public int shrinkZ;
	public int width;
	public int height;
	Color color;

	public void load(ImageReader2 pt, Color target, boolean reverse, int dz) {
		shrinkZ = Math.max(0, shrinkZ);
		this.shrinkZ = dz;

		zMax = pt.zMax - dz;
		width = pt.frameWidth(zMax);
		height = pt.frameHeight(zMax);
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZbaseTile tb = getTile(fi.fi);
			ImageReaderTile2 tt = pt.getTile(fi.fi);

			tb.load(tt.getImage(), target, reverse);
			tt.sleep();
		}
	}

	@Override
	protected ZbaseTile createTile(int fi) {
		return new ZbaseTile(fi, this);
	}

	public boolean isRoad(int x, int y, int z) {

		ZbaseTile tile = getTile(x, y, z);

		if (tile == null)
			return false;

		x = globalToTile(x, z);
		y = globalToTile(y, z);
		return tile.canWalk(x, y);
	}

	@Override
	public void fromBytes(byte[] data) {
//		data = FileUtil.uncompress(data);
		ZintBuffer zb = new ZintBuffer(data);
		shrinkZ = zb.readZint();
		width = zb.readZint();
		height = zb.readZint();
		zMax = zb.readZint();
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZbaseTile tb = getTile(fi.fi);
			tb.fromBytes(zb.readBytesZ());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(shrinkZ);
		zb.writeZint(width);
		zb.writeZint(height);
		zb.writeZint(zMax);
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZbaseTile tb = getTile(fi.fi);
			zb.writeBytesZ(tb.toBytes());
		}
		byte[] result = zb.toBytes();
//		result = FileUtil.compress(result);
		return result;
	}

	public void draw(BufferedImage canvas, Color paint, int dxGlobal, int dyGlobal, int dz) {

		if (zMax >= dz)
			for (FoldIndex fi : FoldIndex.getAllFiOnLayer(zMax - dz)) {
				ZbaseTile tile = getTile(fi.fi);

				tile.draw(canvas, paint, dxGlobal, dyGlobal, 0);
			}
		else {
			ZbaseTile tile = getTile(FoldIndex.create(0, 0, 0).fi);
			tile.draw(canvas, paint, dxGlobal, dyGlobal, dz);
		}
	}

	public void draw(int z) throws IOException {
		int half = FoldIndex.sideLength(z) * 512 / 2;
		BufferedImage bi = ImageUtil.newImage(half * 2, half * 2, Color.BLACK);

		for (int i = -half; i < half; i++)
			for (int j = -half; j < half; j++) {
				// if(i==4&&j==4&&z==1)
				// log.info("stop");
				if (this.isRoad(i, j, z)) {
					bi.setRGB(i + half, j + half, Color.red.getRGB());
				}
			}

		// ImageUtil.writePng(bi, "z:/map/��װ��v8/shop.base.debug" + z +
		// ".png");
		Debug.draw(bi, "Z:/temp/测试图/废弃小镇v2/" + z + ".png");
	}

	// public void drawSmall(BufferedImage bi, Color target, int dx, int dy, int
	// dz) {
	//
	// int w = bi.getWidth();
	// int h = bi.getHeight();
	// int b = FoldIndex.sideLength(zMax) * tileSide;
	// int scale = FastMath.pow2(dz);
	//
	// for (FoldIndex fi : FoldIndex.getAllFiOnLayer(zMax)) {
	// ZbaseTile tile = getTile(fi.fi);
	// int x = fi.xp * tileSide + (w - b) / 2 + dx;
	// int y = fi.yp * tileSide + (h - b) / 2 + dy;
	// BufferedImage tileImage = bi.getSubimage(x, y, tileSide / scale,
	// tileSide / scale);
	//
	// // tile.draw(tileImage, target, scale);
	// }
	// }
}
