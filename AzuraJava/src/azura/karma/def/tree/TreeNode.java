package azura.karma.def.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class TreeNode implements ZintCodecI {
	public int id;
	private TreeNode parent;
	private List<TreeNode> childList = new ArrayList<TreeNode>();

	// decoding cache
	private int idParent;
	private List<Integer> childIdList;

	// ================== encoding ============
	@Override
	public void readFrom(ZintReaderI reader) {
		childIdList = new ArrayList<Integer>();

		id = reader.readInt();
		idParent = reader.readInt();
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			int idChild = reader.readInt();
			childIdList.add(idChild);
		}
	}

	void readNodes(Tree tree) {
		parent = tree.getNode(idParent);
		for (int cid : childIdList) {
			TreeNode child = tree.getNode(cid);
			// childList.add(child);
			addChild(child);
		}
		idParent = 0;
		childIdList = null;
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeInt(id);
		if (parent == null)
			writer.writeInt(0);
		else
			writer.writeInt(parent.id);
		writer.writeZint(childList.size());
		for (TreeNode child : childList) {
			writer.writeInt(child.id);
		}
	}

	// ==================== encoding end ===========
	public TreeNode getParent() {
		return parent;
	}

	public void addChild(TreeNode child) {
		if (child == this)
			throw new Error();

		child.parent = this;
		childList.add(child);
	}

	public LinkedList<TreeNode> getUpList() {
		LinkedList<TreeNode> result = new LinkedList<TreeNode>();
		TreeNode c = this;
		while (c.parent != null) {
			c = c.parent;
			result.addFirst(c);
		}
		return result;
	}

	/**
	 * @return without self
	 */
	public LinkedList<TreeNode> getBroadFirstList() {
		LinkedList<TreeNode> result = new LinkedList<TreeNode>();
		LinkedList<TreeNode> cache = new LinkedList<TreeNode>();
		cache.add(this);
		while (cache.isEmpty() == false) {
			TreeNode p = cache.pollFirst();
			if (p != this)
				result.add(p);
			cache.addAll(p.childList);
		}
		return result;
	}

}
