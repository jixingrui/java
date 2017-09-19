package azura.banshee.zebra2.zimage2;

import java.awt.image.BufferedImage;
import java.util.List;

import azura.banshee.chessboard.fi.PyramidFi;
import azura.banshee.zebra2.RectC;
import azura.banshee.zebra2.Zebra2BranchI;
import azura.banshee.zebra2.tif2.ImageReader2;
import azura.banshee.zebra2.tif2.ImageReaderTile2;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import common.algorithm.FoldIndex;
import common.collections.RectanglePlus;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZimageLarge2 extends PyramidFi<ZimageLargeTile2> implements
		ZintCodecI, Zebra2BranchI {

	private RectanglePlus bbc;

	@Override
	protected ZimageLargeTile2 createTile(int fi) {
		return new ZimageLargeTile2(fi, this);
	}

	public void load(Zatlas2 atlas, ImageReader2 source) {
		this.zMax = source.zMax;
		this.bbc=source.bbc;

		List<FoldIndex> list = FoldIndex.getAllFiInPyramid(source.zMax);

		// list.parallelStream().forEach(fi -> {
		list.forEach(fi -> {
			ImageReaderTile2 ttLand = source.getTile(fi.fi);
			ZimageLargeTile2 tl = this.getTile(fi.fi);

			BufferedImage land512 = ttLand.getImage();
			tl.load(atlas, land512);
		});
		// System.out.println("one large image loaded");
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		zMax = reader.readZint();
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZimageLargeTile2 tile = this.getTile(fi.fi);
			tile.readFrom(reader);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(zMax);
		for (FoldIndex fi : FoldIndex.getAllFiInPyramid(zMax)) {
			ZimageLargeTile2 tile = this.getTile(fi.fi);
			tile.writeTo(writer);
		}
	}

	@Override
	public RectC getBoundingBox() {
		RectC bb=new RectC();
		bb.width=bbc.getWidth();
		bb.height=bbc.getHeight();
		bb.xc=bbc.getCenterX();
		bb.yc=bbc.getCenterY();
		return bb;
	}

}
