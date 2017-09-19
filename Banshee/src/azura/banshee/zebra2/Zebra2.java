package azura.banshee.zebra2;

import java.awt.Color;
import java.io.File;
import java.util.Set;

import azura.banshee.util.FileMatrix2;
import azura.banshee.util.TextureType;
import azura.banshee.zebra2.tif2.ImageReader2;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import azura.banshee.zebra2.zimage2.ZimageLarge2;
import azura.banshee.zebra2.zimage2.ZimageSmall2;
import azura.banshee.zebra2.zmotion2.ZHline2;
import azura.banshee.zebra2.zmotion2.ZVline2;
import azura.banshee.zebra2.zmotion2.Zmatrix2;
import azura.gallerid.GalPack5;
import azura.gallerid.GalPackI5;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.util.FileUtil;

public class Zebra2 implements BytesI, GalPackI5 {
	private static final int formatVersion = 2016020910;
	public static final int BLANK = 0;
	public static final int IMAGE_BITMAP = 1;
	public static final int IMAGE_SMALL = 2;
	public static final int IMAGE_LARGE = 3;
	public static final int HLINE = 4;
	public static final int VLINE = 5;
	public static final int MATRIX = 6;

	public Zatlas2 atlas = new Zatlas2(2048);
	public RectC boundingBox = new RectC();
	public Zebra2BranchI op = new Zblank2();

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		long fileFormatVersion = zb.readInt();
		if (fileFormatVersion != formatVersion)
			throw new Error();

		boundingBox.readFrom(zb);
		atlas.fromBytes(zb.readBytesZ());

		int type = zb.readZint();
		switch (type) {
		case BLANK:
			op = new Zblank2();
			break;
		case IMAGE_BITMAP:
			op = new ZimageBitmap2();
			break;
		case IMAGE_SMALL:
			op = new ZimageSmall2();
			break;
		case IMAGE_LARGE:
			op = new ZimageLarge2();
			break;
		case HLINE:
			op = new ZHline2();
			break;
		case VLINE:
			op = new ZVline2();
			break;
		case MATRIX:
			op = new Zmatrix2();
			break;
		default:
			throw new Error("unknown type");
		}
		op.readFrom(zb);
	}

	@Override
	public byte[] toBytes() {
		int type = BLANK;
		if (op instanceof ZimageBitmap2)
			type = IMAGE_BITMAP;
		if (op instanceof ZimageSmall2)
			type = IMAGE_SMALL;
		else if (op instanceof ZimageLarge2)
			type = IMAGE_LARGE;
		else if (op instanceof ZHline2)
			type = HLINE;
		else if (op instanceof ZVline2)
			type = VLINE;
		else if (op instanceof Zmatrix2)
			type = MATRIX;

		ZintBuffer zb = new ZintBuffer();
		zb.writeInt(formatVersion);
		boundingBox.writeTo(zb);
		zb.writeBytesZ(atlas.toBytes());
		zb.writeZint(type);
		op.writeTo(zb);
		return zb.toBytes();
	}

	public void loadZimage(File input) {

		ImageReader2 reader = ImageReader2.load(input.getPath(), new Color(0x0, true), true);

		boolean XL = reader.bbc.width > 2048 || reader.bbc.height > 2048;
		boolean L = reader.bbc.width > 1024 || reader.bbc.height > 1024;
		boolean solid = reader.rootType == TextureType.Solid;
		if (XL || (L && solid)) {
			ZimageLarge2 large = new ZimageLarge2();
			large.load(atlas, reader);
			op = large;
		} else {
			ZimageSmall2 small = new ZimageSmall2();
			small.load(atlas, input);
			op = small;
		}
		reader.dispose();

		atlas.seal();
		boundingBox = op.getBoundingBox();
	}

	public void loadZmatrix(FileMatrix2 fm) {

		if (fm.rows > 1 && fm.cols > 1) {
			Zmatrix2 matrix = new Zmatrix2();
			matrix.load(atlas, fm);
			op = matrix;
		} else if (fm.cols > 1) {
			ZHline2 hline = new ZHline2();
			hline.load(atlas, fm);
			op = hline;
		} else if (fm.rows > 1) {
			ZVline2 vline = new ZVline2();
			vline.load(atlas, fm);
			op = vline;
		} else {
			throw new Error("Zebra: folder does not contain a matrix");
		}

		atlas.seal();
		boundingBox = op.getBoundingBox();
	}

	public void packTo(String output) {
		GalPack5 gp = new GalPack5();
		byte[] compress = FileUtil.compress(this.toBytes());
		gp.setMaster(compress);
		this.extractMc5(gp.slaveSet);
		gp.toPack(output);

		gp.cleanUp();
		this.atlas.cleanUp();
	}

	@Override
	public void extractMc5(Set<String> slaveSet) {
		atlas.extractMc5(slaveSet);
	}
}
