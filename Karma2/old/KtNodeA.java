package azura.karma.editor.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.collections.buffer.i.ZintCodecI;

public abstract class KtNodeA implements ZintCodecI {
//	public KarmaTree tree;
	public KtNodeA parent;
	public List<KtNodeA> childList = new ArrayList<KtNodeA>();

	abstract int getId();

	abstract int getIdParent();

	abstract void setIdParent(int idParent);

	public void addChild(KtNodeA child) {
		if(child==this)
			throw new Error();
		
		child.parent = this;
		child.setIdParent(this.getId());
		childList.add(child);
	}

	public LinkedList<KtNodeA> getUpList() {
		LinkedList<KtNodeA> result = new LinkedList<KtNodeA>();
		KtNodeA c = this;
		while (c.parent!=null) {
			c = c.parent;
			result.addFirst(c);
		}
		return result;
	}

	/**
	 * @return without self
	 */
	public LinkedList<KtNodeA> getBroadFirstList() {
		LinkedList<KtNodeA> result = new LinkedList<KtNodeA>();
		LinkedList<KtNodeA> cache = new LinkedList<KtNodeA>();
		cache.add(this);
		while (cache.isEmpty() == false) {
			KtNodeA p = cache.pollFirst();
			if (p != this)
				result.add(p);
			cache.addAll(p.childList);
		}
		return result;
	}
}
