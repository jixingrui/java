package azura.helios6.read;

import azura.helios6.Hnode;

import common.collections.ArrayListAuto;

@SuppressWarnings("serial")
public class JoinList extends ArrayListAuto<Hnode> {

	public boolean hitEnd;
	public int idxOfLastRecord = -1;

	// @Override
	// public void fromBytes(byte[] bytes) {
	// ZintBuffer zb = new ZintBuffer(bytes);
	// hasNext = zb.readBoolean();
	// idxOfLastRecord = zb.readZint();
	// int size = zb.readZint();
	// for (int i = 0; i < size; i++) {
	// Hnode n = new Hnode();
	// n.fromBytes(zb.readBytesZ());
	// add(n);
	// }
	// }
	//
	// @Override
	// public byte[] toBytes() {
	// ZintBuffer zb = new ZintBuffer();
	// zb.writeBoolean(hasNext);
	// zb.writeZint(idxOfLastRecord);
	// zb.writeZint(size());
	// for (int i = 0; i < size(); i++) {
	// zb.writeBytesZ(get(i).toBytes());
	// }
	// return zb.toBytes();
	// }

}
