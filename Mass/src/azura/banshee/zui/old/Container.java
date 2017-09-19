package azura.banshee.zui.old;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Container implements ZintCodecI {
	ZuiX zx = new ZuiX();
	ZuiY zy = new ZuiY();
	ZuiW zw = new ZuiW();
	ZuiH zh = new ZuiH();

	@Override
	public void readFrom(ZintReaderI zb) {
		zx.readFrom(zb);
		zy.readFrom(zb);
		zw.readFrom(zb);
		zh.readFrom(zb);
	}

	@Override
	public void writeTo(ZintWriterI zb) {
		zx.writeTo(zb);
		zy.writeTo(zb);
		zw.writeTo(zb);
		zh.writeTo(zb);
	}

	public void move(int i, int j) {
	}
}
