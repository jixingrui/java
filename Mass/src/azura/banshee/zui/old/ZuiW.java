package azura.banshee.zui.old;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZuiW implements ZintCodecI {
	public DataMode dm;
	public int width;

	@Override
	public void readFrom(ZintReaderI reader) {
		dm = DataMode.values()[reader.readZint()];
		width = reader.readZint();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(dm.ordinal());
		writer.writeZint(width);
	}
}
