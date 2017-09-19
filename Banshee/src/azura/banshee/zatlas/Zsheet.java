package azura.banshee.zatlas;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import azura.banshee.main.ME5Old;
import azura.banshee.util.AtfUtilOld;
import azura.gallerid.GalFile;
import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;
import common.graphics.RectanglePacker;

public class Zsheet implements BytesI, GalPackI {
	public static final int maxSize = 1024;
	public static Logger log = Logger.getLogger(Zsheet.class);

	private RectanglePacker<Zframe> packer = new RectanglePacker<Zframe>(
			maxSize, maxSize,0);
	public ArrayList<Zframe> frameList = new ArrayList<Zframe>();
	private boolean isFull = false;
	/**
	 * {png,atf_win,atf_and,atf_ios}
	 */
	private ME5Old[] me5List = new ME5Old[4];

	public int idx;

	public Zsheet() {

	}

	public Zsheet(int idx) {
		this.idx = idx;
	}

	public boolean isEmpty() {
		return frameList.size() == 0;
	}

	/**
	 * @param frame
	 * @return success
	 */
	public boolean tryAdd(Zframe frame) {
		if (isFull || frame == null)
			throw new Error();

		if (frame.blank_key_delta == 0)
			return true;

		frame.rectOnSheet = packer.insert(frame.compact.getWidth(),
				frame.compact.getHeight(), frame);
		if (frame.rectOnSheet == null) {
			isFull = true;
			return false;
		} else {
			frameList.add(frame);
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
		packer = new RectanglePacker<>(sheetSize, sheetSize,0);
		for (Zframe zf : frameList) {
			zf.rectOnSheet = packer.insert(zf.compact.getWidth(),
					zf.compact.getHeight(), zf);
			if (zf.rectOnSheet == null) {
				return false;
			}
		}
		return true;
	}

	public BufferedImage reviveImage() {
		BufferedImage image = null;
		byte[] png = GalFile.read(me5List[0].toString());
		try {
			image = ImageIO.read(new ByteArrayInputStream(png));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public void draw() {

		// log.debug("draw start " + idx);

		BufferedImage sheet = new BufferedImage(packer.getWidth(),
				packer.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (Zframe zf : frameList) {
			ImageUtil.copyPixels(zf.compact, sheet, zf.rectOnSheet.x,
					zf.rectOnSheet.y);
			zf.compact = null;
		}

		byte[] png = ImageUtil.encodePng(sheet);
		byte[][] atf = AtfUtilOld.image2atf(sheet);

		// platformList = new ME5[4];
		me5List[0] = new ME5Old(png).isCpu(true);
		me5List[1] = new ME5Old(atf[0]).isWindows(true);
		me5List[2] = new ME5Old(atf[1]).isAndroid(true);
		me5List[3] = new ME5Old(atf[2]).isIos(true);

		GalFile.write(me5List[0].toString(), png);
		GalFile.write(me5List[1].toString(), atf[0]);
		GalFile.write(me5List[2].toString(), atf[1]);
		GalFile.write(me5List[3].toString(), atf[2]);

//		try {
//			ImageIO.write(sheet, "png", new File("c:\\temp\\"+FastMath.random(0, 88888)+".png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		// log.debug("draw end " + idx);
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		me5List[0] = new ME5Old(zb.readUTFZ());
		me5List[1] = new ME5Old(zb.readUTFZ());
		me5List[2] = new ME5Old(zb.readUTFZ());
		me5List[3] = new ME5Old(zb.readUTFZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ(me5List[0].toString());
		zb.writeUTFZ(me5List[1].toString());
		zb.writeUTFZ(me5List[2].toString());
		zb.writeUTFZ(me5List[3].toString());
		return zb.toBytes();
	}

	@Override
	public void extractMe5To(GalPack gp) {
		gp.addSlave(me5List[0].toString());
		gp.addSlave(me5List[1].toString());
		gp.addSlave(me5List[2].toString());
		gp.addSlave(me5List[3].toString());
	}

}
