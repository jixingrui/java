package azura.banshee.zimage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import azura.banshee.chessboard.land.PyramidBg;
import azura.banshee.chessboard.land.TileBg;
import azura.banshee.chessboard.tif.LargeImageReader;
import azura.banshee.chessboard.tif.LargeImageTile;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.algorithm.FoldIndex;
import common.collections.buffer.BytesI;
import common.collections.buffer.ZintBuffer;

public class ZimageLargeOld implements BytesI, GalPackI {

	PyramidBg pl;

	public void load(LargeImageReader pt) throws IOException {

		pl = new PyramidBg();

		List<FoldIndex> list = FoldIndex.getAllFiInPyramid(pt.zMax);

		for (int i = 0; i < list.size(); i++) {
			int fi = list.get(i).fi;

			LargeImageTile ttLand = pt.getTile(fi);
			TileBg tl = pl.getTile(fi);

			BufferedImage land256 = ttLand.getImage();
			tl.process(land256);
			System.out.print(i + "/" + list.size() + "\t");
			if ((i + 1) % 30 == 0)
				System.out.println();
		}
	}

	@Override
	public void fromBytes(byte[] data) {
	}

	@Override
	public byte[] toBytes() {
		if (pl == null) {
			ZintBuffer zb = new ZintBuffer();
			zb.writeZint(-1);
			return zb.toBytes();
		} else
			return pl.toBytes();
	}

	public void extractMc5(GalPack gp) {
		if (pl != null)
			pl.extractMc5(gp);
	}

}
