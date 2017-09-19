package azura.helios6.write;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

public class Op implements BytesI, Comparable<Op> {
	public OpE type;

	public int one = -1, to = -1;

	public Op(OpE type) {
		this.type = type;
	}

	public Op(byte[] data) {
		fromBytes(data);
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintReaderI zb = new ZintBuffer(data);
		int typeIdx = zb.readZint();
		type = OpE.values()[typeIdx];
		one = zb.readZint();
		to = zb.readZint();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(type.ordinal());
		zb.writeZint(one);
		zb.writeZint(to);
		return zb.toBytes();
	}

	@Override
	public int compareTo(Op o) {
		if (this.type.ordinal() > o.type.ordinal())
			return 1;
		else if (this.type.ordinal() < o.type.ordinal())
			return -1;
		else
			return 0;
	}
}
