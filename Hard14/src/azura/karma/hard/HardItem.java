package azura.karma.hard;

import azura.helios6.Hnode;
import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import zz.karma.Hard.K_Item;

public class HardItem extends K_Item implements Comparable<HardItem> {

	public static KarmaSpace ksHard;

	public static final HardItem fromNode(Hnode node) {
		return new HardItem(ksHard, node);
	}

	private Hnode node;

	private HardItem(KarmaSpace space, Hnode node) {
		super(space);
		this.node = node;
		init();
		fromBytes(node.getData());
	}

	public Hnode getNode() {
		node.setData(this.toBytes());
		return node;
	}

	public Long getNodeId() {
		return node.getId();
	}

	private void init() {
		name = "";
		nameTail = "";
		numChildren = 0;
		color = -1;
		data = null;
		sortValue = 0;
	}

	public String longName() {
		return name + nameTail;
	}

	@Override
	public String toString() {
		return "HardItem name=" + name + " id=" + node.getId();
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

	@Override
	public void fromKarma(Karma karma) {
		try {
			super.fromKarma(karma);
		} catch (Error e) {
			init();
		}
	}

	@Override
	public int compareTo(HardItem other) {
		if (this.sortValue > other.sortValue)
			return 1;
		else if (this.sortValue < other.sortValue)
			return -1;
		else
			return 0;
	}

}
