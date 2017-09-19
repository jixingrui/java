package azura.helios5.batch;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import azura.helios5.HeliosNode;
import azura.helios5.join.Join;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintReaderI;

public class Batch implements BytesI {

	static Logger log = Logger.getLogger(Batch.class);

	public String sessionId;
	private boolean sealed;
	public boolean success;
	private boolean isReturn;

	public List<HeliosNode> nodeList = new ArrayList<>();
	public List<Op> opList = new ArrayList<>();
	public BitSet saveNodes = new BitSet();
	public BitSet deleteNodes = new BitSet();

	public Join condition;
	public boolean exists_not;

	public Batch() {
		sessionId = UUID.randomUUID().toString();
	}

	public Batch(byte[] data) {
		fromBytes(data);
	}

	public Batch link(HeliosNode from, HeliosNode to) {
		Op op = new Op(OpE.link);
		op.from = put(from);
		op.to = put(to);
		opList.add(op);
		return this;
	}

	public Batch save(HeliosNode node) {
		Op op = new Op(OpE.save);
		op.from = put(node, true);
		op.to = op.from;
		opList.add(op);
		saveNodes.set(op.from);
		return this;
	}

	public Batch delink(HeliosNode from, HeliosNode to) {
		if (from.id == 0 || to.id == 0)
			throw new IllegalArgumentException("Batch: cannot delink new node");

		Op op = new Op(OpE.delink);
		op.from = put(from);
		op.to = put(to);
		opList.add(op);
		return this;
	}

	public Batch delete(HeliosNode node) {
		if (node.id == 0)
			throw new IllegalArgumentException("Batch: cannot delete new node");

		Op op = new Op(OpE.delete);
		op.from = put(node);
		op.to = op.from;
		opList.add(op);
		deleteNodes.set(op.from);
		return this;
	}

	public Batch swap(HeliosNode from, HeliosNode to) {
		Op op = new Op(OpE.swap);
		op.from = put(from);
		op.to = put(to);
		opList.add(op);
		return this;
	}

	@Override
	public void fromBytes(byte[] data) {
		if (sessionId != null)
			throw new IllegalAccessError("Batch: use constructor");

		ZintReaderI zb = new ZintBuffer(data);
		sessionId = zb.readUTFZ();
		sealed = zb.readBoolean();
		isReturn = zb.readBoolean();
		success = zb.readBoolean();
		int nodeCount = zb.readZint();
		for (int i = 0; i < nodeCount; i++) {
			HeliosNode node = new HeliosNode(zb.readBytesZ());
			nodeList.add(node);
		}
		int opCount = zb.readZint();
		for (int i = 0; i < opCount; i++) {
			Op op = new Op(zb.readBytesZ());
			opList.add(op);
		}
	}

	@Override
	public byte[] toBytes() {
		if (!sealed)
			throw new Error("not sealed");

		ZintBuffer zb = new ZintBuffer();
		zb.writeUTFZ(sessionId);
		zb.writeBoolean(sealed);
		zb.writeBoolean(isReturn);
		zb.writeBoolean(success);
		zb.writeZint(nodeList.size());
		for (int i = 0; i < nodeList.size(); i++) {
			HeliosNode node = nodeList.get(i);
			HeliosNode c = node.clone();
			if (!saveNodes.get(i))
				c.data = null;
			zb.writeBytesZ(c.toBytes());
		}
		zb.writeZint(opList.size());
		for (Op op : opList) {
			zb.writeBytesZ(op.toBytes());
		}
		return zb.toBytes();
	}

	public boolean eat(Batch result) {
		if (this == result)
			return true;

		if (this.sessionId != result.sessionId || !result.isReturn
				|| this.nodeList.size() != result.nodeList.size())
			throw new IllegalAccessError("Batch: return mismatch");

		for (int i = 0; i < nodeList.size(); i++) {
			HeliosNode original = nodeList.get(i);
			HeliosNode ret = result.nodeList.get(i);
			original.id = ret.id;
		}

		return true;
	}

	private int put(HeliosNode node) {
		return put(node, false);
	}

	private int put(HeliosNode node, boolean replace) {
		if (node == null)
			throw new IllegalArgumentException("Batch: node cannot be null");

		int idx = nodeList.indexOf(node);
		if (idx == -1) {
			idx = nodeList.size();
			nodeList.add(node);
		} else if (replace) {
			nodeList.set(idx, node);
		}
		return idx;
	}

	public void fail() {
		isReturn = true;
		success = false;
		nodeList.clear();
		opList.clear();
		saveNodes.clear();
		deleteNodes.clear();
	}

	public void success() {
		isReturn = true;
		success = true;
		opList.clear();
		saveNodes.clear();
		deleteNodes.clear();
	}

	public boolean isEmpty() {
		return opList.isEmpty();
	}

	public Batch seal() {
		if (sealed)
			throw new Error("cannot seal twice");

		// all references must be in the node list
		for (Op op : opList) {
			if ((op.from != 0 && op.from >= nodeList.size())
					|| (op.to != 0 && op.to >= nodeList.size())) {
				throw new Error(
						"batch construction failed: contains unknown reference");
			}
		}
		sealed = true;

		return this;
	}

	public Join ifExist() {
		exists_not = true;
		condition = new Join();
		return condition;
	}

	public Join ifNotExist() {
		exists_not = false;
		condition = new Join();
		return condition;
	}

	public Batch ifLinked(HeliosNode from, HeliosNode to) {
		return this;
	}

	public Batch ifNotLinked(HeliosNode from, HeliosNode to) {
		return this;
	}

	public boolean isSealed() {
		return sealed;
	}
}
