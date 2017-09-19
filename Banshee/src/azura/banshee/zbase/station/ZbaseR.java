package azura.banshee.zbase.station;

import java.awt.Color;
import java.awt.image.BufferedImage;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.zebra2.tif2.ImageReader2;
import azura.banshee.zebra2.tif2.ImageReaderTile2;
import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ZbaseR extends PyramidFi<ZbaseTileR> implements BytesI {

	public int shrinkZ;
	public int width;
	public int height;

	public int getStation(int x, int y) {
		x >>= shrinkZ;
		y >>= shrinkZ;
		ZbaseTileR tile = getTile(x, y, zMax);
		x = globalToTile(x, zMax);
		y = globalToTile(y, zMax);
		return tile.getStation(x, y);
	}

	public void load(ImageReader2 pt, Color target, boolean reverse, int dz) {
		shrinkZ = Math.max(0, shrinkZ);
		this.shrinkZ = dz;

		zMax = pt.zMax - dz;
		width = pt.frameWidth(zMax);
		height = pt.frameHeight(zMax);
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZbaseTileR tb = getTile(fi.fi);
			ImageReaderTile2 tt = pt.getTile(fi.fi);

			tb.load(tt.getImage(), target, reverse);
			tt.sleep();
		}
	}

	@Override
	protected ZbaseTileR createTile(int fi) {
		return new ZbaseTileR(fi, this);
	}

	public Pixel getPixel(int x, int y) {
		ZbaseTileR tile = getTile(x, y, zMax);

		if (tile == null)
			return null;

		x = globalToTile(x, zMax);
		y = globalToTile(y, zMax);
		return tile.getPixel(x, y);
	}

	public boolean isRoad(int x, int y, int z) {

		ZbaseTileR tile = getTile(x, y, z);

		if (tile == null)
			return false;

		x = globalToTile(x, z);
		y = globalToTile(y, z);
		return tile.canWalk(x, y);
	}

	@Override
	public void fromBytes(byte[] data) {
		// data = FileUtil.uncompress(data);
		ZintBuffer zb = new ZintBuffer(data);
		shrinkZ = zb.readZint();
		width = zb.readZint();
		height = zb.readZint();
		zMax = zb.readZint();
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZbaseTileR tb = getTile(fi.fi);
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
			ZbaseTileR tb = getTile(fi.fi);
			zb.writeBytesZ(tb.toBytes());
		}
		byte[] result = zb.toBytes();
		// result = FileUtil.compress(result,true);
		return result;
	}

	public void draw(BufferedImage canvas, int dxGlobal, int dyGlobal, int dz) {

		if (zMax >= dz)
			for (FoldIndex fi : FoldIndex.getAllFiOnLayer(zMax - dz)) {
				ZbaseTileR tile = getTile(fi.fi);

				tile.draw(canvas, dxGlobal, dyGlobal, 0);
			}
		else {
			ZbaseTileR tile = getTile(FoldIndex.create(0, 0, 0).fi);
			tile.draw(canvas, dxGlobal, dyGlobal, dz);
		}
	}

}
