package azura.banshee.chessboard.mask;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.tif.ImageReader;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.algorithm.FoldIndex;
import common.collections.RectanglePlus;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class Zmask extends PyramidFi<ZmaskTile> implements BytesI, GalPackI {

	private RectanglePlus boundingBox = new RectanglePlus();
	public HashMap<Integer, MaskGem> color_MaskGem = new HashMap<Integer, MaskGem>();

	@Override
	protected ZmaskTile createTile(int fi) {
		return new ZmaskTile(fi, this);
	}

	public boolean load(File input) throws IOException {
		if (!input.isFile())
			return false;

		String maskPath = FileUtil.getNoExt(input.getPath()) + ".mask.tif";
		File maskF = new File(maskPath);

		if (!maskF.exists())
			return false;

		ImageReader pLand = ImageReader.load(input.getPath(), true);

		ImageReader pMask = ImageReader.load(maskPath, false);

		if (pLand.zMax < pMask.zMax)
			throw new Error("mask cannot be larger than land");

		create(pMask);

		List<FoldIndex> list = FoldIndex.getAllFiInPyramid(zMax);

		System.out.println();
		for (int i = 0; i < list.size(); i++) {
			FoldIndex fi = list.get(i);

			ZmaskTile tm = getTile(fi.fi);
			tm.processTile(pLand);

			System.out.print(i + "/" + list.size() + " ");
			if ((i + 1) % 15 == 0)
				System.out.println();
		}
		System.out.println();

		pMask.dispose();

		pLand.dispose();

		return true;
	}

	// public static Zmask load(ImageReader pLand, ImageReader pMask) {
	//
	// if (pLand.zMax < pMask.zMax)
	// throw new Error("mask cannot be larger than land");
	//
	// Zmask zmask = new Zmask();
	// zmask.create(pMask);
	//
	// List<FoldIndex> list = FoldIndex.getAllFiInPyramid(pMask.zMax);
	//
	// System.out.println();
	// for (int i = 0; i < list.size(); i++) {
	// FoldIndex fi = list.get(i);
	//
	// ZmaskTile tm = zmask.getTile(fi.fi);
	// tm.processTile(pLand);
	//
	// System.out.print(i + "/" + list.size() + " ");
	// if ((i + 1) % 15 == 0)
	// System.out.println();
	// }
	// System.out.println();
	//
	// return zmask;
	// }

	public void create(ImageReader reader) {

		zMax = reader.zMax;

		this.boundingBox = reader.bbc;

		int up = boundingBox.y;
		int down = boundingBox.getFloor();
		int left = boundingBox.x;
		int right = boundingBox.getRight();

		// gems
		for (int j = down; j >= up; j--)
			for (int i = left; i <= right; i++) {
				int color = reader.getRGB(i, j, reader.zMax);
				if (((color & 0xffffff) != 0)
						&& !color_MaskGem.containsKey(color)) {
					MaskGem gem = MaskGem.create(i, j, reader, color);
					color_MaskGem.put(color, gem);
					// log.info("color found: " + Hex.getHex(color));
				}
			}
		log.info("Total color: " + color_MaskGem.size());

		// shards
		makeShards(color_MaskGem.values());
	}

	private void makeShards(Collection<MaskGem> gems) {

		for (MaskGem gem : gems) {
			gem.toShards(this, zMax);
		}
		for (int z = zMax - 1; z >= 0; z--) {
			List<MaskGem> nextGems = new ArrayList<MaskGem>();
			for (MaskGem gem : gems) {
				MaskGem shrink = gem.shrink();
				if (shrink != null) {
					nextGems.add(shrink);
					shrink.toShards(this, z);
				} else {
					log.info("shard gone");
				}
			}
			gems = nextGems;
		}
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(boundingBox.x);
		zb.writeZint(boundingBox.y);
		zb.writeZint(boundingBox.width);
		zb.writeZint(boundingBox.height);
		zb.writeZint(zMax);
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZmaskTile tile = this.getTile(fi.fi);
			zb.writeBytesZ(tile.toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public void extractMe5To(GalPack gp) {
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZmaskTile tm = getTile(fi.fi);
			tm.extractMe5To(gp);
		}
	}

	@Override
	public void fromBytes(byte[] data) {
	}

	public void dispose() {
	}

	public static void main(String[] args) throws IOException {
		PropertyConfigurator.configure("log4j.properties");
		Logger.getLogger("io.netty").setLevel(Level.WARN);
		log.info("start");

		String maskPath = "Z:/map/��װ��v8/shop.mask.tif";
		ImageReader reader = ImageReader.load(maskPath, false);
		Zmask mask = new Zmask();
		mask.create(reader);

		// shards
		for (int z = mask.zMax; z >= 0; z--) {
			// int side = FoldIndex.sideLength(z) * mask.tileSide;
			BufferedImage out = ImageUtil.newImage(reader.frameWidth(z),
					reader.frameHeight(z), new Color(0xff000000, true));
			for (FoldIndex fi : FoldIndex.getAllFiOnLayer(z)) {
				ZmaskTile tile = mask.getTile(fi.fi);
				tile.draw(out);
			}
			ImageUtil.writePng(out, "Z:/map/��װ��v8/shop.shard" + z + ".png");
		}
	}

	// Collection<MaskGem> gems = mask.color_MaskGem.values();
	// for (MaskGem gem : gems) {
	// gem.toShards(mask, mask.zMax);
	// }
	// drawGems(gems, mask.zMax, reader.getFrameWidth(0),
	// reader.getFrameHeight(0));
	// for (int z = mask.zMax - 1; z >= 0; z--) {
	// List<MaskGem> nextGems = new ArrayList<MaskGem>();
	// for (MaskGem gem : gems) {
	// MaskGem shrink = gem.shrink();
	// if (shrink != null) {
	// nextGems.add(shrink);
	// shrink.toShards(mask, z);
	// } else {
	// log.info("shard gone");
	// }
	// }
	// gems = nextGems;
	// drawGems(gems, z, reader.getFrameWidth(mask.zMax - z),
	// reader.getFrameHeight(mask.zMax - z));
	// }
	// private static void drawGems(Collection<MaskGem> gems, int z, int width,
	// int height) throws IOException {
	// BufferedImage out = ImageUtil.newImage(width, height, new Color(
	// 0xff000000, true));
	// for (MaskGem gem : gems) {
	// gem.drawFill(out);
	// }
	// ImageUtil.writePng(out, "Z:/map/��װ��v8/shop.gem" + z + ".png");
	// }

}