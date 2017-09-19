package azura.banshee.chessboard.base;

import java.io.File;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.tif.PyramidLargeImage;
import azura.banshee.main.Neck;

import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.ZintReaderI;
import common.util.FileUtil;

public class PyramidBaseOld extends PyramidFi<TileBaseOld> {
	static int tileSide = 256 / 8;

	private int scale;

	public PyramidBaseOld(int zMax, int scale) {
		// super.init(levelMax);
		super.zMax = zMax;
		this.scale = scale;
	}

	public PyramidBaseOld() {
	}

	@Override
	protected TileBaseOld createTile(int fi) {
		return new TileBaseOld(fi, this);
	}

	public static PyramidBaseOld load(String fileName, int scale) {
		PyramidLargeImage pt = PyramidLargeImage.load(fileName, tileSide, false);
		PyramidBaseOld pb = new PyramidBaseOld(pt.zMax, scale);

		pb.load(pt);
		pt.dispose();

		return pb;
	}

	public void load(byte[] data) {
		ZintReaderI zb = new ZintBuffer(data);
		super.zMax = zb.readZint();
		for (FoldIndex fi : fiIterator()) {
			TileBaseOld tc = getTile(fi.fi);
			tc.load(zb.readBytes());
		}
	}

	public void load(PyramidLargeImage pt) {
		double sqrt2 = Math.sqrt(2);
		int side = pt.pyramidSide;
		for (int i = 0; i < side; i++)
			for (int j = 0; j < side; j++) {
				int color = pt.getRGB2n(i, j);
				int greenValue = (color & 0x0000ff00) >>> 8;
				if (greenValue == 0)
					continue;

				setHTop(i, j, (byte) (greenValue - 100));

				double[] top = new double[] { i - side / 2, j - side / 2 };
				double[] flat = Neck.topToFlat(top, 0);
				double h = ((greenValue - 100) * scale / 100 / sqrt2);
				double jj = (flat[1] + side / 2 - h / 8);
				byte hv = (byte) (h * 100 / scale);
				setHFlat(i, (int) jj, hv);

				// if(greenValue<100)
				// Trace.trace(greenValue+" "+hv);
			}
	}

	private void setHFlat(int x, int y, byte value) {
		TileBaseOld tc = getTile(x / tileSide, y / tileSide, zMax);
		tc.setFlat(x % tileSide, y % tileSide, value);
	}

	private void setHTop(int x, int y, byte value) {
		TileBaseOld tc = getTile(x / tileSide, y / tileSide, zMax);
		tc.setTop(x % tileSide, y % tileSide, value);
	}

	public byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(scale);
		zb.writeZint(super.zMax);
		for (FoldIndex fi : fiIterator()) {
			TileBaseOld tile = this.getTile(fi.fi);
			zb.writeBytes(tile.encode());
		}

		return zb.toBytes();
	}

	public void writeToFile(File input) {
		byte[] data = FileUtil.compress(encode());
		String fileName = input.getParent() + "/"
				+ input.getParentFile().getName() + ".base";
		FileUtil.write(fileName, data);
	}
}
