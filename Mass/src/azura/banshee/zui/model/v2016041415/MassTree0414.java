package azura.banshee.zui.model.v2016041415;

import java.util.HashMap;

import azura.banshee.zui.model.ScreenSetting;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class MassTree0414 implements ZintCodecI {

	public ScreenSetting ss = new ScreenSetting();
	public MassTreeN0414 root;
	public HashMap<String, MassTreeN0414> path_MassTreeN = new HashMap<>();

	public MassTree0414() {
		root = new MassTreeN0414(null);
		root.tree = this;
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		ss.fromBytes(reader.readBytesZ());
		root.readFrom(reader);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeBytesZ(ss.toBytes());
		root.writeTo(writer);
	}

}
