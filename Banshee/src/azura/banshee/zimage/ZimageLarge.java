package azura.banshee.zimage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.chessboard.tif.ImageReader;
import azura.banshee.chessboard.tif.ImageTile;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ZimageLarge extends PyramidFi<ZimageLargeTile> implements BytesI,
		GalPackI {

	@Override
	protected ZimageLargeTile createTile(int fi) {
		return new ZimageLargeTile(fi, this);
	}

	public void extractMe5To(GalPack gp) {
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZimageLargeTile tile = this.getTile(fi.fi);
			tile.extractMe5To(gp);
		}
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(zMax);
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZimageLargeTile tile = this.getTile(fi.fi);
			zb.writeBytesZ(tile.toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] data) {
	}

	private volatile int counter;

	public void load(ImageReader source) throws IOException {
		this.zMax = source.zMax;

		List<FoldIndex> list = FoldIndex.getAllFiInPyramid(source.zMax);

		counter = 0;
		list.parallelStream().forEach(fi -> {
			// int fi = list.get(i).fi;
				counter++;
				ImageTile ttLand = source.getTile(fi.fi);
				ZimageLargeTile tl = this.getTile(fi.fi);

				BufferedImage land256 = ttLand.getImage();
				try {
					tl.process(land256);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.print(counter + "/" + list.size() + " ");
				if ((counter + 1) % 15 == 0)
					System.out.println();
			});
		System.out.println();

		// for (int i = 0; i < list.size(); i++) {
		// int fi = list.get(i).fi;
		//
		// ImageTile ttLand = source.getTile(fi);
		// ZimageLargeTile tl = this.getTile(fi);
		//
		// BufferedImage land256 = ttLand.getImage();
		// tl.process(land256);
		// System.out.print(i + "/" + list.size() + " ");
		// if ((i + 1) % 15 == 0)
		// System.out.println();
		// }
		// System.out.println();
	}

}
