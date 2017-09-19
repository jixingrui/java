package azura.banshee.zui.old;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZuiY implements ZintCodecI {
	public DataMode dm;
	public int y;
	public AlignY ay;

	@Override
	public void readFrom(ZintReaderI reader) {
		dm = DataMode.values()[reader.readZint()];
		y = reader.readZint();
		ay = AlignY.values()[reader.readZint()];
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(dm.ordinal());
		writer.writeZint(y);
		writer.writeZint(ay.ordinal());
	}
}
