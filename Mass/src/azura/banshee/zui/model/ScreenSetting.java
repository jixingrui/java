package azura.banshee.zui.model;

import common.collections.bitset.abs.ABSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class ScreenSetting implements BytesI {
//	public static final int lockWidth = 0;
//	public static final int lockHeight = 1;

	public int designWidth = 1280;
	public int designHeight = 720;
	public ABSet bsProp = new ABSet();

	public ScreenSetting() {
//		bsProp.set(lockWidth, true);
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer reader = new ZintBuffer(bytes);
		designWidth = reader.readZint();
		designHeight = reader.readZint();
		bsProp.fromBytes(reader.readBytesZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer writer = new ZintBuffer();
		writer.writeZint(designWidth);
		writer.writeZint(designHeight);
		writer.writeBytesZ(bsProp.toBytes());
		return writer.toBytes();
	}

}
