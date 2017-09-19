package azura.helios5.batch;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

public class Op implements BytesI {
	public OpE type;

	public int from = -1, to = -1;

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
		from = zb.readZint();
		to = zb.readZint();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(type.ordinal());
		zb.writeZint(from);
		zb.writeZint(to);
		return zb.toBytes();
	}
}
