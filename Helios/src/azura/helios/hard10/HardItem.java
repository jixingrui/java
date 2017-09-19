package azura.helios.hard10;

import azura.helios5.HeliosNode;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

public class HardItem implements BytesI {

	private HeliosNode node;
	public String name;
	public int numChildren;
	/**
	 * True data, no header from HardItem or Node
	 */
	public byte[] dataPure = new byte[0];

	public HardItem() {
		// name = "new" + FastMath.random(0, 999);
	}

	public HardItem fromNode(HeliosNode node) {
		this.node = node;
		if (node.data.length>0)
			fromBytes(node.data);
		return this;
	}

	public HeliosNode getNode(boolean syncData) {
		if (syncData)
			node.data = toBytes();
		return node;
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ(name);
		zb.writeZint(numChildren);
		zb.writeBytesZ(dataPure);
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintReaderI zb = new ZintBuffer(data);
		name = zb.readUTFZ();
		numChildren = zb.readZint();
		this.dataPure = zb.readBytesZ();
	}

	@Override
	public String toString() {
		return "HardItem name=" + name + " idNode=" + node.id;
	}

	@Override
	public boolean equals(Object o) {
		HardItem other = (HardItem) o;
		if (this == other)
			return true;
		else if (this.node == null || other.node == null)
			return false;
		else
			return this.node.equals(other.node);
	}

	/**
	 * @param node
	 *            created by Hard
	 * @return original data
	 */
	public static byte[] extractPureData(HeliosNode node) {
		HardItem hi = new HardItem();
		hi.fromNode(node);
		return hi.dataPure;
	}

}
