package azura.banshee.zebra2.zimage2;

import java.io.File;

import azura.banshee.zebra2.RectC;
import azura.banshee.zebra2.Zebra2BranchI;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import azura.banshee.zebra2.zmotion2.Zline2;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZimageSmall2 implements ZintCodecI, Zebra2BranchI {

	private Zline2 line = new Zline2();

	@Override
	public void readFrom(ZintReaderI reader) {
		line.readFrom(reader);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		line.writeTo(writer);
	}

	public void load(Zatlas2 atlas, File input) {
		File[] source = new File[1];
		source[0] = input;
		line.load(atlas, source);
	}

	@Override
	public RectC getBoundingBox() {
		return line.getFirstBB();
	}

}
