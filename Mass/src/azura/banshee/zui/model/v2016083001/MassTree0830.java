package azura.banshee.zui.model.v2016083001;

import azura.banshee.zui.model.ScreenSetting;
import azura.banshee.zui.model.v2016073113.MassTree0731;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class MassTree0830 implements ZintCodecI {

	public ScreenSetting ss = new ScreenSetting();
	public MassTreeN0830 root;

	public MassTree0830() {
		root = new MassTreeN0830(null);
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

	public void eat(MassTree0731 old) {
		this.ss = old.ss;
		this.root.eat(old.root);
	}

}
