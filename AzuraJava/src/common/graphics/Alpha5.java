package common.graphics;

import java.awt.image.BufferedImage;

import common.collections.bitset.lbs.LBSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.util.FileUtil;

public class Alpha5 {
	private LBSet lbs0 = new LBSet();
	private LBSet lbs1 = new LBSet();
	private LBSet lbs2 = new LBSet();
	private LBSet lbs3 = new LBSet();

	public Alpha5() {
	}

	public Alpha5(byte[] zip) {
		byte[] raw=FileUtil.uncompress(zip);
		ZintReaderI zb = new ZintBuffer(raw);
		lbs0 = new LBSet(zb.readBytesZ());
		lbs1 = new LBSet(zb.readBytesZ());
		lbs2 = new LBSet(zb.readBytesZ());
		lbs3 = new LBSet(zb.readBytesZ());
	}

	public void put(int color) {
		int trans = color >>> 24;
		if (trans < 48) {
			lbs0.push(true);
			lbs1.push(false);
			lbs2.push(false);
			lbs3.push(false);
		} else if (trans <= 80) {
			lbs0.push(false);
			lbs1.push(true);
			lbs2.push(false);
			lbs3.push(false);
		} else if (trans <= 120) {
			lbs0.push(false);
			lbs1.push(false);
			lbs2.push(true);
			lbs3.push(false);
		} else if (trans <= 220) {
			lbs0.push(false);
			lbs1.push(false);
			lbs2.push(false);
			lbs3.push(true);
		} else {
			lbs0.push(false);
			lbs1.push(false);
			lbs2.push(false);
			lbs3.push(false);
		}

	}

	public void pasteTo(BufferedImage argb) {
		int height = argb.getHeight();
		for (int pos : lbs0) {
			int i = pos / height;
			int j = pos % height;
			argb.setRGB(i, j, 0);
		}

		for (int pos : lbs1) {
			int i = pos / height;
			int j = pos % height;
			int color = argb.getRGB(i, j);
			argb.setRGB(i, j, color & 0xffffff | (48 << 24));
		}

		for (int pos : lbs2) {
			int i = pos / height;
			int j = pos % height;
			int color = argb.getRGB(i, j);
			argb.setRGB(i, j, color & 0xffffff | (100 << 24));
		}

		for (int pos : lbs3) {
			int i = pos / height;
			int j = pos % height;
			int color = argb.getRGB(i, j);
			argb.setRGB(i, j, color & 0xffffff | (150 << 24));
		}
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(lbs0.toBytes());
		zb.writeBytesZ(lbs1.toBytes());
		zb.writeBytesZ(lbs2.toBytes());
		zb.writeBytesZ(lbs3.toBytes());
		byte[] raw= zb.toBytes();
		return FileUtil.compress(raw);
	}
}
