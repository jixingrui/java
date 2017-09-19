package azura.banshee.zui.old;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZuiX implements ZintCodecI {
	public DataMode dm;
	public int x;
	public AlignX ax;

	@Override
	public void readFrom(ZintReaderI reader) {
		dm = DataMode.values()[reader.readZint()];
		x = reader.readZint();
		ax = AlignX.values()[reader.readZint()];
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(dm.ordinal());
		writer.writeZint(x);
		writer.writeZint(ax.ordinal());
	}
}
