package azura.banshee.chessboard.mask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.tif.ImageReader;
import azura.gallerid.GalPackI5;
import common.algorithm.FoldIndex;
import common.collections.RectanglePlus;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.util.FileUtil;

public class Zmask2 extends PyramidFi<ZmaskTile2> implements BytesI, GalPackI5 {

	private RectanglePlus boundingBox = new RectanglePlus();
	public HashMap<Integer, MaskGem2> color_MaskGem2 = new HashMap<Integer, MaskGem2>();

	@Override
	protected ZmaskTile2 createTile(int fi) {
		return new ZmaskTile2(fi, this);
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

			ZmaskTile2 tm = getTile(fi.fi);
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
						&& !color_MaskGem2.containsKey(color)) {
					MaskGem2 gem = MaskGem2.create(i, j, reader, color);
					color_MaskGem2.put(color, gem);
				}
			}
		log.info("Total color: " + color_MaskGem2.size());

		// shards
		makeShards(color_MaskGem2.values());
	}

	private void makeShards(Collection<MaskGem2> gems) {

		for (MaskGem2 gem : gems) {
			gem.toShards(this, zMax);
		}
		for (int z = zMax - 1; z >= 0; z--) {
			List<MaskGem2> nextGems = new ArrayList<MaskGem2>();
			for (MaskGem2 gem : gems) {
				MaskGem2 shrink = gem.shrink();
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
			ZmaskTile2 tile = this.getTile(fi.fi);
			zb.writeBytesZ(tile.toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] data) {
	}

	public void dispose() {
	}

	@Override
	public void extractMc5(Set<String> slaveSet) {
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZmaskTile2 tm = getTile(fi.fi);
			tm.extractMc5(slaveSet);
		}
	}

	public void cleanUp() {
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZmaskTile2 tm = getTile(fi.fi);
			tm.cleanUp();
		}
	}

}