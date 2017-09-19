package azura.helios6;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import azura.helios6.write.Op;
import azura.helios6.write.OpE;

import common.collections.SortedArrayList;

public class Batch {

	static Logger log = Logger.getLogger(Batch.class);

	boolean sealed;
	private boolean success;

	List<Hnode> nodeList = new ArrayList<>();
	Collection<Op> opList = new SortedArrayList<Op>();
	BitSet writeNodes = new BitSet();
	BitSet deleteNodes = new BitSet();

	public Batch link(Hnode from, Hnode to) {
		Op op = new Op(OpE.link);
		op.one = put(from);
		op.to = put(to);
		opList.add(op);
		return this;
	}

	public Batch save(Hnode node) {
		Op op = new Op(OpE.save);
		op.one = put(node);
		opList.add(op);
		writeNodes.set(op.one);

		if (deleteNodes.get(op.one))
			throw new Error("cannot save and delete node");
		return this;
	}

	public Batch delink(Hnode from, Hnode to) {
		if (from.getId() == 0 || to.getId() == 0)
			throw new IllegalArgumentException("Batch: cannot delink new node");

		Op op = new Op(OpE.delink);
		op.one = put(from);
		op.to = put(to);
		opList.add(op);
		return this;
	}

	public Batch delete(Hnode node) {
		if (node.getId() == 0)
			throw new IllegalArgumentException("Batch: cannot delete new node");
		if (node.getId() < 0)
			throw new IllegalArgumentException("Batch: cannot delete tag node");

		Op op = new Op(OpE.delete);
		op.one = put(node);
		opList.add(op);
		deleteNodes.set(op.one);

		if (writeNodes.get(op.one))
			throw new Error("cannot save and delete node");
		return this;
	}

	public Batch swap(Hnode from, Hnode to) {
		if (from.getId() == 0 || to.getId() == 0)
			throw new IllegalArgumentException("Batch: cannot swap new node");

		Op op = new Op(OpE.swap);
		op.one = put(from);
		op.to = put(to);
		opList.add(op);
		writeNodes.set(op.one);
		writeNodes.set(op.to);

		return this;
	}

	private int put(Hnode node) {
		if (sealed)
			throw new Error("Batch: node sealed");

		if (node == null)
			throw new IllegalArgumentException("Batch: node cannot be null");

		int idx = nodeList.indexOf(node);
		if (idx == -1) {
			if (node.getId() > 0) {
				for (Hnode stored : nodeList) {
					if (stored.getId().equals(node.getId()))
						throw new IllegalArgumentException(
								"Batch: different nodes have same id");
				}
			}
			idx = nodeList.size();
			nodeList.add(node);
			if (node.getId() == 0) {
				// new node should be written
				writeNodes.set(idx);
			}
		}
		return idx;
	}

	public void fail() {
		success = false;
		nodeList.clear();
		opList.clear();
		writeNodes.clear();
		deleteNodes.clear();
	}

	public void success() {
		success = true;
		opList.clear();
		writeNodes.clear();
		deleteNodes.clear();
	}

	public boolean isEmpty() {
		return opList.isEmpty();
	}

	public boolean isSuccess() {
		return success;
	}

}
