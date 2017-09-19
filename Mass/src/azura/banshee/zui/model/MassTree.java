package azura.banshee.zui.model;

import azura.banshee.zui.model.v2016041415.MassTree0414;
import azura.banshee.zui.model.v2016061713.MassTree0617;
import azura.banshee.zui.model.v2016073113.MassTree0731;
import azura.banshee.zui.model.v2016083001.MassTree0830;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class MassTree implements BytesI {

	public MassTree0830 v0830 = new MassTree0830();

	@Override
	public void fromBytes(byte[] bytes) {
		v0830 = new MassTree0830();

		ZintBuffer zb = new ZintBuffer();
		zb.fromBytes(bytes);
		int fileFormat = zb.readInt();
		if (fileFormat == 2016083001) {
			v0830.readFrom(zb);
		} else if (fileFormat == 2016073113) {
			MassTree0731 v0731 = new MassTree0731();
			v0731.readFrom(zb);
			v0830.eat(v0731);
		} else if (fileFormat == 2016061713) {
			MassTree0617 v0617 = new MassTree0617();
			v0617.readFrom(zb);
			MassTree0731 v0731 = new MassTree0731();
			v0731.eat(v0617);
			v0830.eat(v0731);
		} else if (fileFormat == 2016041415) {
			MassTree0414 v0414 = new MassTree0414();
			v0414.readFrom(zb);
			MassTree0617 v0617 = new MassTree0617();
			v0617.eat(v0414);
			MassTree0731 v0731 = new MassTree0731();
			v0731.eat(v0617);
			v0830.eat(v0731);
		} else {
			throw new Error();
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeInt(2016083001);
		v0830.writeTo(zb);
		return zb.toBytes();
	}

}
