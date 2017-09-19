package azura.banshee.zmask;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import azura.banshee.zanim.Zframe;
import azura.banshee.zanim.Zsheet;
import azura.gallerid.Gal_Pack;
import azura.gallerid.Gal_Pack_userI;

import common.collections.buffer.ByteSerializable;
import common.collections.buffer.ZintBuffer;
import common.util.FileUtil;

public class ZmaskTile implements ByteSerializable, Gal_Pack_userI {

	private int frameCount;
	private List<Zsheet> layers = new ArrayList<>();
	private Rectangle bb;

	public void load(File[] files) throws IOException {
		this.frameCount = files.length;
		Zsheet bottom = new Zsheet();

		for (int i = 0; i < files.length; i++) {
			BufferedImage source = ImageIO.read(files[i]);
			Zframe zf = new Zframe().load(source,true);
			bottom.add(zf);
		}

		Zsheet current = bottom;
		while (current != null) {
			if (!current.pack())
				throw new Error(
						"Zanime: source image too large or frame too long");
			layers.add(current);
			current = current.getHalf();
		}
		bb = layers.get(0).getBoundingBox();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		args = new String[] { "D:\\executable\\zanime" };
		File folder = new File(args[0]);
		ZmaskTile za = new ZmaskTile();
		za.load(folder.listFiles());

		Gal_Pack gp = new Gal_Pack();
		byte[] raw = za.toBytes();
		gp.setMaster(FileUtil.compress(raw));
		za.extractMd5(gp);

		gp.encode("D:\\executable\\output.zanime");
	}

	@Override
	public void fromBytes(byte[] data) {
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeUTF("zanime");
		zb.writeZint(bb.x);
		zb.writeZint(bb.y);
		zb.writeZint(bb.width);
		zb.writeZint(bb.height);
		zb.writeZint(frameCount);
		zb.writeZint(layers.size());
		for (int i = 0; i < layers.size(); i++) {
			zb.writeBytes(layers.get(i).toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public void extractMd5(Gal_Pack gp) {
		for (Zsheet zl : layers) {
			zl.extractMd5(gp);
		}
	}

	public void clear() {
		for (Zsheet zl : layers) {
			zl.clear();
		}
	}

}
