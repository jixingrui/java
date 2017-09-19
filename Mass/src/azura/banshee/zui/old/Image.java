package azura.banshee.zui.old;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Image implements ZintCodecI {
	public String me5;
	public int width, height;

	@Override
	public void readFrom(ZintReaderI reader) {
		me5 = reader.readUTFZ();
		width = reader.readZint();
		height = reader.readZint();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeUTFZ(me5);
		writer.writeZint(width);
		writer.writeZint(height);
	}

	public void setSkin(String string) {
	}
}
