package azura.banshee.main;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import azura.banshee.chessboard.tif.ImageReader;
import azura.banshee.zimage.Zimage;
import azura.banshee.zmotion.Zmatrix;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Zebra implements BytesI, GalPackI {
	public static final int ZEMPTY = 0;
	public static final int ZIMAGE = 1;
	public static final int ZMATRIX = 2;

	public ZebraBranchI branch;

	// These meta data are not about how the Zebra was created. It's about how
	// it
	// will be used.
	public int x, y;
	/**
	 * [0~359]
	 */
	public int angle = 0;
	/**
	 * scale%
	 */
	public int scale = 100;
	public int fps = 12;

	public Rectangle getBoundingBox() {
		return branch.getBoundingBox();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		int type = zb.readZint();
		switch (type) {
		case ZIMAGE:
			branch = new Zimage();
			break;
		case ZMATRIX:
			branch = new Zmatrix();
			break;
		default:
			throw new Error("unknown type");
		}
		branch.fromBytes(zb.readBytesZ());
		x = zb.readZint();
		y = zb.readZint();
		angle = zb.readZint();
		scale = zb.readZint();
		fps = zb.readZint();
	}

	@Override
	public byte[] toBytes() {
		if (branch == null)
			throw new Error();

		int type = ZEMPTY;
		if (branch instanceof Zimage)
			type = ZIMAGE;
		else if (branch instanceof Zmatrix)
			type = ZMATRIX;

		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(type);
		zb.writeBytesZ(branch.toBytes());
		zb.writeZint(x);
		zb.writeZint(y);
		zb.writeZint(angle);
		zb.writeZint(scale);
		zb.writeZint(fps);
		return zb.toBytes();
	}

	@Override
	public void extractMe5To(GalPack gp) {
		branch.extractMe5To(gp);
	}

	public void load(File input) throws IOException {
		if (input.isFile()) {
			ImageReader reader = ImageReader.load(input.getPath(), true);

			Zimage z = new Zimage();
			z.load(reader);
			reader.dispose();

			branch = z;
		} else if (input.isDirectory()) {
			Zmatrix zm = new Zmatrix();
			zm.load(input);

			branch = zm;
		}
	}

}
