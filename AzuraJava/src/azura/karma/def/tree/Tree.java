package azura.karma.def.tree;

import com.esotericsoftware.kryo.util.IntMap;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Tree implements BytesI {
	private IntMap<TreeNode> id_Node = new IntMap<>();
	private TreeNode root;

	public Tree(int idRoot) {
		root = new TreeNode();
		root.id = idRoot;
		id_Node.put(root.id, root);
	}

	public TreeNode getRoot() {
		return root;
	}

	public TreeNode getNode(int id) {
		return id_Node.get(id);
	}

	public void addNode(TreeNode tn) {
		TreeNode old = id_Node.put(tn.id, tn);
		if (old != null)
			throw new Error();
	}

	// =============== encoding ============
	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer reader = new ZintBuffer(bytes);
		root = null;
		id_Node.clear();

		int idRoot = reader.readInt();
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			TreeNode tn = new TreeNode();
			tn.readFrom(reader);
			addNode(tn);
		}
		root = getNode(idRoot);

		for (TreeNode tn : id_Node.values()) {
			tn.readNodes(this);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer writer = new ZintBuffer();
		writer.writeInt(root.id);
		writer.writeZint(id_Node.size);
		for (TreeNode tn : id_Node.values()) {
			tn.writeTo(writer);
		}
		return writer.toBytes();
	}
}
