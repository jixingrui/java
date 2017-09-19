package azura.banshee.zimage;

import java.awt.image.BufferedImage;
import java.io.IOException;

import azura.banshee.chessboard.fi.TileFi;
import azura.banshee.main.ME5Old;
import azura.banshee.util.AtfUtilOld;
import azura.banshee.util.TextureType;
import azura.gallerid.GalFile;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class ZimageLargeTile extends TileFi<ZimageLargeTile> implements
		GalPackI, BytesI {

	private TextureType plateType = TextureType.Init;
	/**
	 * {png/jpg,atf_win,atf_and,atf_ios}
	 */
	public ME5Old[] me5List = new ME5Old[4];

	public ZimageLargeTile(int fi, ZimageLarge pyramid) {
		super(fi, pyramid);
	}

	public void process(BufferedImage land256) throws IOException {

		if (plateType != TextureType.Init)
			throw new Error();

		plateType = TextureType.check(land256);
		byte[][] atf;
		if (plateType == TextureType.Empty) {
			// me5List[0] = "";
			// me5List[1] = "";
			// me5List[2] = "";
			// me5List[3] = "";
		} else {
			byte[] png_jpg = null;
			if (plateType == TextureType.Alpha) {
				png_jpg = ImageUtil.encodePng(land256);
			} else if (plateType == TextureType.Solid) {
				png_jpg = ImageUtil.encodeJpg(land256, 0.85f);
			} else {
				throw new Error();
			}

			atf = AtfUtilOld.image2atf(land256);

			me5List[0] = new ME5Old(png_jpg).isCpu(true);
			me5List[1] = new ME5Old(atf[0]).isWindows(true);
			me5List[2] = new ME5Old(atf[1]).isAndroid(true);
			me5List[3] = new ME5Old(atf[2]).isIos(true);

			GalFile.write(me5List[0].toString(), png_jpg);
			GalFile.write(me5List[1].toString(), atf[0]);
			GalFile.write(me5List[2].toString(), atf[1]);
			GalFile.write(me5List[3].toString(), atf[2]);
		}
	}

	public void extractMe5To(GalPack gp) {

		if (plateType == TextureType.Empty)
			return;

		gp.addSlave(me5List[0].toString());
		gp.addSlave(me5List[1].toString());
		gp.addSlave(me5List[2].toString());
		gp.addSlave(me5List[3].toString());
	}

	public byte[] toBytes() {

		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(plateType.ordinal());

		if (plateType != TextureType.Empty) {
			zb.writeUTFZ(me5List[0].toString());
			zb.writeUTFZ(me5List[1].toString());
			zb.writeUTFZ(me5List[2].toString());
			zb.writeUTFZ(me5List[3].toString());
		}
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
	}
}
