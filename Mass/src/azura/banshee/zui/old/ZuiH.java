package azura.banshee.zui.old;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZuiH implements ZintCodecI {
	public DataMode dm;
	public int height;

	@Override
	public void readFrom(ZintReaderI reader) {
		dm = DataMode.values()[reader.readZint()];
		height = reader.readZint();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(dm.ordinal());
		writer.writeZint(height);
	}
}
