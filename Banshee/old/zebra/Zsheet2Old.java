package azura.banshee.zebra2.zatlas2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import azura.banshee.util.Png2Atf;
import azura.banshee.util.TextureType;
import azura.gallerid.GalFile;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;
import common.algorithm.FastMath;
import common.algorithm.ME5;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;
import common.graphics.RectanglePacker2;
import common.util.FileUtil;

public class Zsheet2Old implements BytesI, GalPackI {
	public static final int maxSize = 2048;
	public static Logger log = Logger.getLogger(Zsheet2Old.class);
	public final int idxInAtlas;

	private RectanglePacker2<Zframe2> packer = new RectanglePacker2<Zframe2>(maxSize, maxSize);
	public ArrayList<Zframe2> frameList = new ArrayList<Zframe2>();
	private boolean isFull = false;
	/**
	 * {bitmap,atf_win,atf_and,atf_ios}
	 */
	private ME5[] me5List = new ME5[4];
	private int width;
	private int height;

	public Zsheet2Old(int idxInAtlas) {
		this.idxInAtlas = idxInAtlas;
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
		while (success && sheetSize >= 32) {
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

		boolean allSolid = true;
		for (Zframe2 frame : frameList) {
			if (frame.textureType == TextureType.Alpha)
				allSolid = false;
			continue;
		}

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
			if (allSolid) {
				sheet = new BufferedImage(packer.getWidth(), packer.getHeight(), BufferedImage.TYPE_INT_RGB);
			} else {
				sheet = new BufferedImage(packer.getWidth(), packer.getHeight(), BufferedImage.TYPE_INT_ARGB);
			}
			for (Zframe2 zf : frameList) {
				ImageUtil.copyPixels(zf.getCompact(), sheet, zf.rectOnSheet.x, zf.rectOnSheet.y);
			}
		}

		width = packer.getWidth();
		height = packer.getHeight();

		byte[] bitmap = null;
		if (singleFit && !allSolid) {
			bitmap = ImageUtil.encodePng(frameList.get(0).getCompact()); // GalFile.read(frameList.get(0).me5.toString());
		} else if (allSolid) {
			bitmap = ImageUtil.encodeJpg(sheet, 0.9f);
		} else {
			bitmap = ImageUtil.encodePng(sheet);
		}
		// byte[][] atf = AtfUtil.image2atf(sheet);
		byte[][] atf = null;
		try {
			atf = Png2Atf.convert_dxt_etc_pvr(sheet);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		me5List[0] = new ME5(bitmap, ME5.bitmap);
		me5List[1] = new ME5(atf[0], ME5.atf_win);
		me5List[2] = new ME5(atf[1], ME5.atf_android);
		me5List[3] = new ME5(atf[2], ME5.atf_ios);

		GalFile.write(me5List[0].toString(), bitmap);
		GalFile.write(me5List[1].toString(), atf[0]);
		GalFile.write(me5List[2].toString(), atf[1]);
		GalFile.write(me5List[3].toString(), atf[2]);

		log.info("sheet area usage " + packer.getAreaUsedPercentage() + "%");

		// debug2(me5List[0].toString(), !allSolid);
	}

	private void debug2(String me5, boolean png_jpg) {
		byte[] sheet = GalFile.read(me5);
		String path = "z:/temp/zebra2/debug/" + FastMath.random(0, 88888);
		if (png_jpg)
			path += ".png";
		else
			path += ".jpg";
		FileUtil.write(path, sheet);
		log.debug("debug image at " + path);
	}

	private void debug(BufferedImage sheet) {
		try {
			File debug = new File("z:\\temp\\debug\\" + FastMath.random(0, 88888) + ".png");
			debug.mkdirs();
			ImageIO.write(sheet, "png", debug);
			log.debug("debug image at " + debug.getPath());
		} catch (IOException e) {
			// e.printStackTrace();
			log.error("debug location not reachable");
		}
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		width = zb.readZint();
		height = zb.readZint();
		me5List[0] = new ME5(zb.readUTFZ());
		me5List[1] = new ME5(zb.readUTFZ());
		me5List[2] = new ME5(zb.readUTFZ());
		me5List[3] = new ME5(zb.readUTFZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(width);
		zb.writeZint(height);
		zb.writeUTFZ(me5List[0].toString());
		zb.writeUTFZ(me5List[1].toString());
		zb.writeUTFZ(me5List[2].toString());
		zb.writeUTFZ(me5List[3].toString());
		return zb.toBytes();
	}

	@Override
	public void extractMc5To(GalPack gp) {
		gp.addSlave(me5List[0].toString());
		gp.addSlave(me5List[1].toString());
		gp.addSlave(me5List[2].toString());
		gp.addSlave(me5List[3].toString());
	}

}
