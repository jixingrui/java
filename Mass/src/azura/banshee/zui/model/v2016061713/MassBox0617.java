package azura.banshee.zui.model.v2016061713;

import azura.banshee.zui.model.IntPercent;
import azura.banshee.zui.model.v2016041415.MassBox0414;
import common.collections.bitset.abs.ABSet;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class MassBox0617 implements BytesI {

	public ABSet propSet = new ABSet();

	/**
	 * [1,2,3] [4,5,6] [7,8,9]
	 */
	public int align = 5;

	public IntPercent x = new IntPercent();
	public IntPercent y = new IntPercent();
	public IntPercent w = new IntPercent();
	public IntPercent h = new IntPercent();

	public String me5_zebra;

	public int from_device;
	public String from_mass;
	
	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		propSet.fromBytes(zb.readBytesZ());
		align = zb.readZint();
		x.readFrom(zb);
		y.readFrom(zb);
		w.readFrom(zb);
		h.readFrom(zb);
		me5_zebra = zb.readUTFZ();
		from_device = zb.readZint();
		from_mass = zb.readUTFZ();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(propSet.toBytes());
		zb.writeZint(align);
		x.writeTo(zb);
		y.writeTo(zb);
		w.writeTo(zb);
		h.writeTo(zb);
		zb.writeUTFZ(me5_zebra);
		zb.writeZint(from_device);
		zb.writeUTFZ(from_mass);
		return zb.toBytes();
	}

	public void eat(MassBox0414 old) {
		this.propSet = old.propSet;
		this.align = old.align;
		this.x = old.x;
		this.y = old.y;
		this.w = old.w;
		this.h = old.h;
		this.me5_zebra = old.me5_zebra;
		this.from_device = 0;
		this.from_mass = "";
	}

}
