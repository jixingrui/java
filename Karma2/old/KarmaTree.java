package azura.karma.editor.tree;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class KarmaTree implements BytesI {
	static Logger log = Logger.getLogger(KarmaTree.class);

	public HashMap<Integer, KtNode> id_node = new HashMap<Integer, KtNode>();

	public KarmaTree() {
		init();
	}

	private void init() {
		id_node.clear();
		addNode(new KtNode());
	}

	// public KtNode newNode() {
	// KtNode tn = new KtNode();
	// // tn.tree = this;
	// return tn;
	// }

	public KtNode getRoot() {
		return id_node.get(0);
	}

	public void addNode(KtNode node) {
		id_node.put(node.getId(), node);
	}

	@Override
	public void fromBytes(byte[] bytes) {
		init();
		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			KtNode current = new KtNode();
			current.readFrom(zb);
			id_node.put(current.getId(), current);

			KtNode parent = getNode(current.getIdParent());
			parent.addChild(current);
		}
	}

	@Override
	public byte[] toBytes() {
		LinkedList<KtNodeA> list = getRoot().getBroadFirstList();

		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(list.size());
		while (list.isEmpty() == false) {
			KtNodeA current = list.poll();
			current.writeTo(zb);
			// log.info("id="+current.getId()+" idParent="+current.getIdParent());
		}
		return zb.toBytes();
	}

	public KtNode getNode(int id) {
		return id_node.get(id);
	}
}
