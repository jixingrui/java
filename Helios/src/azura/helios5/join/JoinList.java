package azura.helios5.join;

import azura.helios5.HeliosNode;
import common.collections.ArrayListAuto;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

@SuppressWarnings("serial")
public class JoinList extends ArrayListAuto<HeliosNode> implements BytesI {

	public boolean hasNext;
	public int idxOfLastRecord = -1;

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		hasNext = zb.readBoolean();
		idxOfLastRecord = zb.readZint();
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			HeliosNode n = new HeliosNode();
			n.fromBytes(zb.readBytesZ());
			add(n);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBoolean(hasNext);
		zb.writeZint(idxOfLastRecord);
		zb.writeZint(size());
		for (int i = 0; i < size(); i++) {
			zb.writeBytesZ(get(i).toBytes());
		}
		return zb.toBytes();
	}

}
