package azura.banshee.zebra2;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZimageBitmap2 implements ZintCodecI, Zebra2BranchI {

	@Override
	public void readFrom(ZintReaderI reader) {
	}

	@Override
	public void writeTo(ZintWriterI writer) {
	}

	@Override
	public RectC getBoundingBox() {
		return new RectC();
	}

}
