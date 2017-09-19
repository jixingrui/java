package azura.banshee.zui.model;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class IntPercent implements ZintCodecI {
	
	public boolean int_percent=true;
	public int value=0;

	@Override
	public void readFrom(ZintReaderI reader) {
		int_percent=reader.readBoolean();
		value=reader.readZint();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeBoolean(int_percent);
		writer.writeZint(value);
	}

}
