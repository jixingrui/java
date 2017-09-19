package azura.banshee.zebra2.zatlas2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import azura.banshee.util.Png2Atf;
import azura.gallerid.GalFile;
import azura.gallerid.GalPackI5;
import common.algorithm.FastMath;
import common.algorithm.MC5;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;
import common.graphics.RectanglePacker2;

public class Zsheet3 implements BytesI, GalPackI5 {
	// public static int maxSize = 1024;
	private int maxSize;

	public static Logger log = Logger.getLogger(Zsheet3.class);
	public final int idxInAtlas;

	private RectanglePacker2<Zframe2> packer;
	public ArrayList<Zframe2> frameList = new ArrayList<Zframe2>();
	private boolean isFull = false;
	/**
	 * legacy: {bitmap,atf_win,atf_and,atf_ios}
	 */
	private MC5[] mc5List = new MC5[4];
	int width;
	int height;

	public Zsheet3(int idxInAtlas, int maxSize) {
		this.idxInAtlas = idxInAtlas;
		this.maxSize = maxSize;
		this.packer = new RectanglePacker2<Zframe2>(maxSize, maxSize);
	}

	public boolean isEmpty() {
		return frameList.size() == 0;
	}

	/**
	 * @param frame
	 * @return success
	 */
	public boolean tryAdd(Zframe2 frame) {
		if (isFull || frame == null)
			throw new Error();

		if (frame.blank_key_delta == 0)
			return true;

		frame.rectOnSheet = packer.insert(frame, frame.getWidth(), frame.getHeight());
		if (frame.rectOnSheet == null) {
			isFull = true;
			return false;
		} else {
			frameList.add(frame);
			frame.idxSheet = this.idxInAtlas;
			return true;
		}
	}

	public void shrink() {
		boolean success = true;
		int sheetSize = maxSize;
		while (success && sheetSize >= 64) {
			sheetSize /= 2;
			success = tryPack(sheetSize);
		}
		if (!success) {
			sheetSize *= 2;
			tryPack(sheetSize);
		}
	}

	private boolean tryPack(int sheetSize) {
		packer = new RectanglePacker2<>(sheetSize, sheetSize);
		for (Zframe2 zf : frameList) {
			zf.rectOnSheet = packer.insert(zf, zf.getWidth(), zf.getHeight());
			if (zf.rectOnSheet == null) {
				return false;
			}
		}
		return true;
	}

	public void draw() {

		shrink();

		// boolean allSolid = true;
		// for (Zframe2 frame : frameList) {
		// if (frame.textureType == TextureType.Alpha)
		// allSolid = false;
		// continue;
		// }

		boolean singleFit = false;
		if (frameList.size() == 1) {
			Zframe2 zf = frameList.get(0);
			if (zf.getWidth() == zf.getHeight() && zf.getWidth() >= 4 && FastMath.isPowerOfTwo(zf.getWidth())) {
				singleFit = true;
			}
		}

		BufferedImage sheet = null;
		if (singleFit) {
			sheet = frameList.get(0).getCompact();
		} else {
			sheet = new BufferedImage(packer.getWidth(), packer.getHeight(), BufferedImage.TYPE_INT_ARGB);

			for (Zframe2 zf : frameList) {
				ImageUtil.copyPixels(zf.getCompact(), sheet, zf.rectOnSheet.x, zf.rectOnSheet.y);
			}
		}

		width = packer.getWidth();
		height = packer.getHeight();

		byte[] atf = null;
		try {
			atf = Png2Atf.convert_888(sheet);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		mc5List[0] = new MC5(atf);
		mc5List[1] = mc5List[0];
		mc5List[2] = mc5List[0];
		mc5List[3] = mc5List[0];

		GalFile.write(mc5List[0].toString(), atf);

		log.info("block size " + atf.length / 1000 + "k , area usage " + packer.getAreaUsedPercentage() + "%");

		// debug2(me5List[0].toString(), !allSolid);
	}

	// private void debug2(String me5, boolean png_jpg) {
	// byte[] sheet = GalFile.read(me5);
	// String path = "z:/temp/zebra2/debug/" + FastMath.random(0, 88888);
	// if (png_jpg)
	// path += ".png";
	// else
	// path += ".jpg";
	// FileUtil.write(path, sheet);
	// log.debug("debug image at " + path);
	// }

	// private void debug(BufferedImage sheet) {
	// try {
	// File debug = new File("z:\\temp\\debug\\" + FastMath.random(0, 88888) +
	// ".png");
	// debug.mkdirs();
	// ImageIO.write(sheet, "png", debug);
	// log.debug("debug image at " + debug.getPath());
	// } catch (IOException e) {
	// // e.printStackTrace();
	// log.error("debug location not reachable");
	// }
	// }

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		width = zb.readZint();
		height = zb.readZint();
		mc5List[0] = new MC5(zb.readUTFZ());
		mc5List[1] = new MC5(zb.readUTFZ());
		mc5List[2] = new MC5(zb.readUTFZ());
		mc5List[3] = new MC5(zb.readUTFZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(width);
		zb.writeZint(height);
		zb.writeUTFZ(mc5List[0].toString());
		zb.writeUTFZ(mc5List[1].toString());
		zb.writeUTFZ(mc5List[2].toString());
		zb.writeUTFZ(mc5List[3].toString());
		return zb.toBytes();
	}

	@Override
	public void extractMc5(Set<String> slaveSet) {
		slaveSet.add(mc5List[0].toString());
		slaveSet.add(mc5List[1].toString());
		slaveSet.add(mc5List[2].toString());
		slaveSet.add(mc5List[3].toString());
	}

}
