package azura.banshee.zebra2.zatlas2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import azura.gallerid.GalPackI5;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Zatlas2 implements BytesI, GalPackI5 {
	public static Logger log = Logger.getLogger(Zatlas2.class);

	List<Zframe2> frameList = Collections.synchronizedList(new ArrayList<>());
	List<Zsheet3> sheetList = Collections.synchronizedList(new ArrayList<>());
	List<ZGroup2> groupList = Collections.synchronizedList(new ArrayList<>());

	private boolean sealed = false;

	private int sheetSize;

	public Zatlas2(int sheetSize) {
		this.sheetSize = sheetSize;
	}

	public ZGroup2 newGroup() {
		checkSeal();
		ZGroup2 group = new ZGroup2(this);
		groupList.add(group);
		return group;
	}

	public Zframe2 getFrame(int idx) {
		return frameList.get(idx);
	}

	synchronized Zframe2 newFrame() {
		checkSeal();
		Zframe2 frame = new Zframe2(frameList.size(), sheetSize);
		frameList.add(frame);
		return frame;
	}

	synchronized Zsheet3 newSheet() {
		checkSeal();
		Zsheet3 sheet = new Zsheet3(sheetList.size(), sheetSize);
		sheetList.add(sheet);
		return sheet;
	}

	public void seal() {
		checkSeal();

		groupList.parallelStream().forEach(group -> {
			group.seal();
		});

		sheetList.parallelStream().forEach(sheet -> {
			sheet.draw();
		});
	}

	private void checkSeal() {
		if (sealed)
			throw new Error();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Zsheet3 sheet = new Zsheet3(i, sheetSize);
			sheetList.add(sheet);
			sheet.fromBytes(zb.readBytesZ());
		}
		size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Zframe2 frame = new Zframe2(i, sheetSize);
			frameList.add(frame);
			frame.fromBytes(zb.readBytesZ());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(sheetList.size());
		for (Zsheet3 sheet : sheetList) {
			zb.writeBytesZ(sheet.toBytes());
		}
		zb.writeZint(frameList.size());
		for (Zframe2 frame : frameList) {
			zb.writeBytesZ(frame.toBytes());
		}
		return zb.toBytes();
	}

	public void cleanUp() {
		frameList.forEach(frame -> {
			frame.cleanUp();
		});
	}

	@Override
	public void extractMc5(Set<String> slaveSet) {
		for (Zsheet3 sheet : sheetList) {
			sheet.extractMc5(slaveSet);
		}
	}
}
