package azura.banshee.zatlas;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import azura.gallerid.GalPack;
import azura.gallerid.GalPackI;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class Zatlas implements BytesI, GalPackI {
	public static Logger log = Logger.getLogger(Zatlas.class);

	List<Zframe> frameList = new ArrayList<>();
	List<Zsheet> sheetList = new ArrayList<>();

	public void add(Zframe frame) {

		if (frame.blank_key_delta == 0) {
			;
		} else if (frame.compact.getWidth() > Zsheet.maxSize
				|| frame.compact.getHeight() > Zsheet.maxSize)
			throw new Error("Zatlas: frame size must not exceed "
					+ Zsheet.maxSize);

		frame.idx = frameList.size();
		frameList.add(frame);

		Zsheet sheet = null;
		if (sheetList.size() > 0)
			sheet = sheetList.get(sheetList.size() - 1);
		else {
			sheet = new Zsheet(sheetList.size());
			sheetList.add(sheet);
		}

		if (!sheet.tryAdd(frame)) {
			sheet = new Zsheet(sheetList.size());
			sheetList.add(sheet);
			sheet.tryAdd(frame);
		}
		frame.idxSheet = sheetList.size() - 1;
	}

	public void seal() {
		if (sheetList.size() == 0)
			return;

		sheetList.get(sheetList.size() - 1).shrink();

		log.debug("seal");

		sheetList.parallelStream().forEach(sheet -> {
			sheet.draw();
		});
	}

	public Zatlas getHalf() {
		// log.debug("get half start");
		boolean allEmpty = true;
		for (Zframe frame : frameList) {
			if (frame.rectOnSheet.width / 2 >= 16
					&& frame.rectOnSheet.height / 2 >= 16)
				allEmpty = false;
			break;
		}
		if (allEmpty)
			return null;

		Zatlas hAtlas = new Zatlas();
		Zsheet old = null;
		BufferedImage cache = null;
		for (Zframe frame : frameList) {
			if (old != sheetList.get(frame.idxSheet)) {
				old = sheetList.get(frame.idxSheet);
				cache = old.reviveImage();
			}
			Zframe hFrame = frame.getHalf();
			BufferedImage frameImage = ImageUtil.getSubImage(cache,
					frame.rectOnSheet.x, frame.rectOnSheet.y,
					frame.rectOnSheet.width, frame.rectOnSheet.height);
			hFrame.compact = ImageUtil.scale(frameImage, 0.5);
			hAtlas.add(hFrame);
		}
		hAtlas.seal();
		return hAtlas;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Zsheet sheet = new Zsheet();
			sheetList.add(sheet);
			sheet.fromBytes(zb.readBytesZ());
		}
		size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Zframe frame = new Zframe();
			frameList.add(frame);
			frame.fromBytes(zb.readBytesZ());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(sheetList.size());
		for (Zsheet sheet : sheetList) {
			zb.writeBytesZ(sheet.toBytes());
		}
		zb.writeZint(frameList.size());
		for (Zframe frame : frameList) {
			zb.writeBytesZ(frame.toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public void extractMe5To(GalPack gp) {
		for (Zsheet sheet : sheetList) {
			sheet.extractMe5To(gp);
		}
	}

}
