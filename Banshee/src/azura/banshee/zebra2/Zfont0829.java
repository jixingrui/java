package azura.banshee.zebra2;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Zfont0829 implements BytesI {

	public int version = 1608292048;
	// ========== basic ============
	public String text = "";
	public int size = 24;
	public int color = 0xff0000;

	// ========== glow ==========
	public int glowStrength = 4;
	public int glowColor = 0xffffff;

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		version = zb.readInt();
		text = zb.readUTFZ();
		size = zb.readZint();
		color = zb.readInt();
		glowStrength = zb.readZint();
		glowColor = zb.readZint();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeInt(version);
		zb.writeUTFZ(text);
		zb.writeZint(size);
		zb.writeInt(color);
		zb.writeZint(glowStrength);
		zb.writeZint(glowColor);
		return zb.toBytes();
	}

}
