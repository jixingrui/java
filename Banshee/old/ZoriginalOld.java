package azura.banshee.zimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.gallerid.GalFile;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;

import common.collections.buffer.BytesI;
import common.collections.buffer.ZintBuffer;
import common.util.FileUtil;

public class ZoriginalOld implements BytesI, GalPackI {

	String mc5;

	// byte[] data;

	public boolean load(File input) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			return false;
		}
		if (img == null)
			return false;

		byte[] data = FileUtil.read(input);
		mc5 = GalFile.putData(data);

		return true;
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeUTF(mc5);

		ZintBuffer zebra = new ZintBuffer();
		zebra.writeZint(0);
		zebra.writeZint(0);
		zebra.writeZint(0);
		zebra.writeUTF("zoriginal");
		zebra.writeBytes(zb.toBytes());

		return zebra.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
	}

	@Override
	public void extractMc5(GalPack gp) {
		gp.addSlave(mc5);
	}
}
