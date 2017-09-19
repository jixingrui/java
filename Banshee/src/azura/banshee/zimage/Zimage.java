package azura.banshee.zimage;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import azura.banshee.chessboard.tif.ImageReader;
import azura.banshee.main.ZebraBranchI;
import azura.gallerid.GalPack;
import common.algorithm.FoldIndex;
import common.collections.RectanglePlus;
import common.collections.buffer.ZintBuffer;
import common.graphics.ImageUtil;
import common.util.FileUtil;

public class Zimage implements ZebraBranchI {

	/**
	 * relative to the image center
	 */
	RectanglePlus boundingBox=new RectanglePlus();
	ZimageTiny tiny = new ZimageTiny();
	public boolean useLarge;
	ZimageLarge large = new ZimageLarge();

	public void load(ImageReader reader) throws IOException {

		boundingBox = reader.bbc;

		useLarge = reader.zMax > 0;

		BufferedImage sourceTiny = reader.getTile(FoldIndex.create(0, 0, 0).fi)
				.getImage();

		if (useLarge) {
			sourceTiny = ImageUtil.scale(sourceTiny, 0.5);
			large.load(reader);
		}

		tiny.load(sourceTiny);
	}

	public void writeToFile(File file) {
		byte[] raw = toBytes();
		byte[] zip = FileUtil.compress(raw);

		GalPack gp = new GalPack();
		gp.setMaster(zip);
		//		gp.master = new ME5(zip).isWindows(true).isIos(true).isAndroid(true)
		//				.toString();

		extractMe5To(gp);

		String fileName = file.getParentFile().getPath() + "/"
				+ file.getName().substring(0, file.getName().lastIndexOf("."))
				+ ".zimage";
		gp.writeTo(fileName);
		gp.cleanUp();
	}

	public void extractMe5To(GalPack gp) {
		tiny.extractMe5To(gp);
		if (useLarge)
			large.extractMe5To(gp);
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb=new ZintBuffer(data);
		boundingBox.x=zb.readZint();
		boundingBox.y=zb.readZint();
		boundingBox.width=zb.readZint();
		boundingBox.height=zb.readZint();
		tiny.fromBytes(zb.readBytesZ());
		useLarge=zb.readBoolean();
		if(useLarge)
			large.fromBytes(zb.readBytesZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(boundingBox.x);
		zb.writeZint(boundingBox.y);
		zb.writeZint(boundingBox.width);
		zb.writeZint(boundingBox.height);
		zb.writeBytesZ(tiny.toBytes());
		zb.writeBoolean(useLarge);
		if (useLarge)
			zb.writeBytesZ(large.toBytes());
		return zb.toBytes();
	}

	@Override
	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	// @Override
	// public int getWidth() {
	// return Math.max(Math.abs(boundingBox.x),
	// Math.abs(boundingBox.getRight())) * 2 + 1;
	// }
	//
	// @Override
	// public int getHeight() {
	// return Math.max(Math.abs(boundingBox.y),
	// Math.abs(boundingBox.getFloor())) * 2 + 1;
	// }

	// /**
	// * @param args
	// * @throws IOException
	// */
	// public static void main(String[] args) throws IOException {
	// args = new String[] { "e:/zforest/clothshop.tif" };
	// // File folder = new File(args[0]);
	// Zimage z = new Zimage();
	// z.load(args[0]);
	// // za.load(folder.listFiles());
	//
	// // List<FoldIndex> list = z.pyramid.fiIterator();
	// //
	// // for (int i = 0; i < list.size(); i++) {
	// // int fi = list.get(i).fi;
	//
	// // TileLargeImage ttLand = z.pyramid.getTile(fi);
	// // BufferedImage land256 = ttLand.getImage();
	// // }
	//
	// GalPack gp = new GalPack();
	// byte[] raw = z.toBytes();
	// gp.setMaster(FileUtil.compress(raw));
	// z.extractMc5(gp);
	//
	// gp.write("e:/zforest/clothshop.zimage");
	// }

	// public void dispose() {
	// pyramid.dispose();
	// }
}
