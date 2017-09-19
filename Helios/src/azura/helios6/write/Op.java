package azura.helios6.write;

import azura.helios6.Hnode;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

public class Op implements BytesI, Comparable<Op> {
	public OpE type;

	public int one = -1, to = -1;
	// public Hnode oneNode,toNode;

	private Batch batch;

	public Op(OpE type, Batch batch) {
		this.type = type;
		this.batch = batch;
	}

	public Hnode oneNode() {
		return batch.nodeList.get(one);
	}

	public Hnode toNode() {
		return batch.nodeList.get(to);
	}

	// public Op(byte[] data) {
	// fromBytes(data);
	// }

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
