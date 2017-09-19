package azura.helios6;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class TreeN implements ZintCodecI {
	public int id;
	public List<TreeN> childList = new ArrayList<TreeN>();

	// cache
	public Hnode cargo;
	private TreeN parent;

	public TreeN getParent() {
		return parent;
	}

	public void addChild(TreeN child) {
		if (child == this)
			throw new Error();

		child.parent = this;
		childList.add(child);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeInt(id);
		writer.writeZint(childList.size());
		for (TreeN child : childList) {
			child.writeTo(writer);
		}
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		id = reader.readInt();
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			TreeN child = new TreeN();
			this.addChild(child);
			child.readFrom(reader);
		}
	}

	// ========== iteration ============
	public LinkedList<TreeN> getUpList() {
		LinkedList<TreeN> result = new LinkedList<TreeN>();
		TreeN c = this;
		while (c.parent != null) {
			c = c.parent;
			result.addFirst(c);
		}
		return result;
	}

	/**
	 * @return without self
	 */
	public LinkedList<TreeN> getBroadFirstList() {
		LinkedList<TreeN> result = new LinkedList<TreeN>();
		LinkedList<TreeN> cache = new LinkedList<TreeN>();
		cache.add(this);
		while (cache.isEmpty() == false) {
			TreeN p = cache.pollFirst();
			if (p != this)
				result.add(p);
			cache.addAll(p.childList);
		}
		return result;
	}

}
