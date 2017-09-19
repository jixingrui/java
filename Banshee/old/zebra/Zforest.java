package azura.banshee.zforest;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.imageio.ImageIO;

import azura.banshee.chessboard.mask.Zmask;
import azura.banshee.main.Zebra;
import azura.banshee.zbase.Zbase;
import azura.banshee.zbase.Zway;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.algorithm.FastMath;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class Zforest implements BytesI, GalPackI {

	/**
	 * in percent
	 */
	int scale;
	Zebra land = new Zebra();
	Zmask mask = new Zmask();
	Zbase base = new Zbase();
	Zway way = new Zway();
	List<Zitem> itemList = new ArrayList<>();

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		scale = zb.readZint();
		land.fromBytes(zb.readBytesZ());
		mask.fromBytes(zb.readBytesZ());
		base.fromBytes(zb.readBytesZ());
		way.fromBytes(zb.readBytesZ());
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Zitem zp = new Zitem();
			zp.fromBytes(zb.readBytesZ());
			itemList.add(zp);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(scale);
		zb.writeBytesZ(land.toBytes());
		zb.writeBytesZ(mask.toBytes());
		zb.writeBytesZ(base.toBytes());
		zb.writeBytesZ(way.toBytes());
		zb.writeZint(itemList.size());
		for (Zitem zp : itemList) {
			zb.writeBytesZ(zp.toBytes());
		}
		return zb.toBytes();
	}

	// public boolean load(File input) throws IOException {
	// if (!input.isFile())
	// return false;
	//
	// String maskPath = FileUtil.getNoExt(input.getPath()) + ".mask.tif";
	// File maskF = new File(maskPath);
	//
	// land = new Zebra();
	// Zimage zi = new Zimage();
	// land.branch = zi;
	//
	// // StreamImageReader original = StreamImageReader.load(input.getPath());
	// ImageReader reader = ImageReader.load(input.getPath(), true);
	//
	// zi.load(reader);
	//
	// if (maskF.exists()) {
	// ImageReader maskImage = ImageReader.load(maskPath, false);
	// mask = Zmask.load(reader, maskImage);
	// maskImage.dispose();
	// }
	//
	// reader.dispose();
	//
	// return true;
	// }

	public void draw(String output) throws IOException {

		int zUp = 1;
		if (base.zMax > 3) {
			zUp = base.zMax - 3;
		}

		// int side = FastMath.pow2(zbase.zMax - zUp) * 256;

		int scale = FastMath.pow2(zUp);
		BufferedImage bi = ImageUtil.newImage(base.width / scale, base.height
				/ scale, new Color(0x0));

		 base.draw(bi, Color.white, 0, 0, zUp);

		int ww = base.width / 2;
		int hh = base.height / 2;

		for (Zitem item : itemList) {
			BitSet bs = item.zbase.toBitSet();
			Rectangle bb = item.zebra.getBoundingBox();
			int w = bb.width;
			int h = bb.height;
			for (int i = 0; i < w; i++)
				for (int j = 0; j < h; j++) {
					int idx = i * h + j;
					if (bs.get(idx)) {
						int x = (ww + item.zebra.x + bb.x + i) / scale;
						int y = (hh + item.zebra.y + bb.y + j) / scale;
						bi.setRGB(x, y, 0xff000000);
					}
				}
		}

		ImageIO.write(bi, "png", new File(output));
	}

	@Override
	public void extractMc5To(GalPack gp) {
		land.extractMc5To(gp);
		mask.extractMc5To(gp);
		for (Zitem item : itemList) {
			item.extractMc5To(gp);
		}
	}
}
