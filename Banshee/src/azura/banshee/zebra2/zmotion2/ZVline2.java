package azura.banshee.zebra2.zmotion2;

import java.io.File;

import azura.banshee.util.FileMatrix2;
import azura.banshee.zebra2.RectC;
import azura.banshee.zebra2.Zebra2BranchI;
import azura.banshee.zebra2.zatlas2.Zatlas2;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZVline2 implements ZintCodecI, Zebra2BranchI {
	private Zline2 vline = new Zline2();

	@Override
	public void readFrom(ZintReaderI reader) {
		vline.readFrom(reader);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		vline.writeTo(writer);
	}

	public void load(Zatlas2 atlas, FileMatrix2 fm) {
		File[] source = new File[fm.rows];
		for (int i = 0; i < fm.rows; i++) {
			source[i] = fm.matrix[i][0];
		}
		vline.load(atlas, source);
	}

	@Override
	public RectC getBoundingBox() {
		return vline.getAverageBB();
	}

}
