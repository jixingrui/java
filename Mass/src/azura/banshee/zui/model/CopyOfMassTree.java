package azura.banshee.zui.model;

import azura.banshee.zui.model.v2016041415.MassTree0414;
import azura.banshee.zui.model.v2016061713.MassTree0617;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class CopyOfMassTree implements BytesI {

	public static final int formatVersion = 2016061713;

	public MassTree0617 core = new MassTree0617();

	@Override
	public void fromBytes(byte[] bytes) {
		core = new MassTree0617();

		ZintBuffer zb = new ZintBuffer();
		zb.fromBytes(bytes);
		int fileFormat = zb.readInt();
		if (fileFormat == 2016061713) {
			core.readFrom(zb);
		} else if (fileFormat == 2016041415) {
			MassTree0414 tree = new MassTree0414();
			tree.readFrom(zb);
			core.eat(tree);
		} else {
			throw new Error();
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeInt(formatVersion);
		core.writeTo(zb);
		return zb.toBytes();
	}

}
