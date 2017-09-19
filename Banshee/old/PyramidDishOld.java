package azura.banshee.chessboard.dish;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.mask.PyramidMask;
import azura.banshee.chessboard.mask.TileMask;
import azura.banshee.chessboard.tif.PyramidLargeImage;
import azura.banshee.chessboard.tif.TileLargeImage;
import azura.gallerid.GalPack;

import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;
import common.util.FileUtil;

public class PyramidDishOld extends PyramidFi<TileDishOld> {

	private PyramidMask pm;

	public PyramidDishOld(int zMax) {
		super.zMax = zMax;
		pm = new PyramidMask(zMax);
	}

	@Override
	protected TileDishOld createTile(int fi) {
		return new TileDishOld(fi, this);
	}

	public void writeToFile(File folder) {

		byte[] dishRaw = encode();
		byte[] dishZip = FileUtil.compress(dishRaw);

		GalPack gp = new GalPack();
		gp.setMaster(dishZip);

		extractMd5(gp);

		String fileName = folder.getParent() + "/"
				+ folder.getParentFile().getName() + "_" + folder.getName()
				+ ".dish";
		gp.write(fileName);

	}

	private void extractMd5(GalPack gp) {
		for (FoldIndex fi : super.fiIterator()) {
			TileDishOld tile = this.getTile(fi.fi);
			tile.extractMd5(gp);
		}
	}

	private byte[] encode() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(zMax);
		for (FoldIndex fi : super.fiIterator()) {
			TileDishOld tile = this.getTile(fi.fi);
			zb.writeBytes(tile.encode());
		}
		return zb.toBytes();
	}

	public static PyramidDishOld load(String land, String mask) throws IOException {
		PyramidLargeImage ptLand = PyramidLargeImage.load(land, 256, false);
		PyramidLargeImage ptMask = PyramidLargeImage.load(mask, 256, false);

		if (ptLand.zMax != ptMask.zMax)
			throw new IllegalArgumentException("land and mask not match");

		PyramidDishOld pp = new PyramidDishOld(ptLand.zMax);

		pp.pm.create(ptMask);

		List<FoldIndex> list = FoldIndex.getAllFiInPyramid(ptLand.zMax);

		for (int i = 0; i < list.size(); i++) {
			int fi = list.get(i).fi;

			TileLargeImage ttLand = ptLand.getTile(fi);
			TileMask tm = pp.pm.getTile(fi);
			TileDishOld tp = pp.getTile(fi);

			BufferedImage land256 = ttLand.getImage();
			tp.process(land256, tm.shardList);
			System.out.print(i + "/" + list.size() + "\t");
		}
		ptLand.dispose();
		ptMask.dispose();

		return pp;
	}

}
