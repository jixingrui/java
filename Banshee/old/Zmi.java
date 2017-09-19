package azura.banshee.zforest.old;

import java.io.File;
import java.io.IOException;

import azura.banshee.chessboard.mask.Zmask;
import azura.banshee.chessboard.tif.ImageReader;
import azura.banshee.main.Zebra;
import azura.banshee.zimage.Zimage;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.util.FileUtil;

public class Zmi implements BytesI, GalPackI {

	Zebra land = new Zebra();
	Zmask mask = new Zmask();

	public boolean load(File input) throws IOException {
		if (!input.isFile())
			return false;

		String maskPath = FileUtil.getNoExt(input.getPath()) + ".mask.tif";
		File maskF = new File(maskPath);

		land = new Zebra();
		Zimage zi = new Zimage();
		land.branch = zi;

		// StreamImageReader original = StreamImageReader.load(input.getPath());
		ImageReader reader = ImageReader.load(input.getPath(), 
				true);

		zi.load(reader);

		if (maskF.exists()) {
			ImageReader maskImage = ImageReader.load(maskPath, 
					false);
			mask = Zmask.load(reader, maskImage);
			maskImage.dispose();
		}

		reader.dispose();

		return true;
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytes(land.toBytes());
		zb.writeBytes(mask.toBytes());
		return zb.toBytes();
	}

	@Override
	public void extractMe5To(GalPack gp) {
		land.extractMe5To(gp);
		mask.extractMe5To(gp);
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		land.fromBytes(zb.readBytes());
		mask.fromBytes(zb.readBytes());
	}

}
