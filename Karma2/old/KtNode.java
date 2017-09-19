package azura.karma.editor.tree;

import java.util.LinkedList;

import azura.helios.hard10.HardItem;
import azura.helios5.HeliosNode;
import azura.karma.editor.def.KarmaDefPack;

import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KtNode extends KtNodeA {
	public KarmaDefPack karma;
	public HardItem hi;
	public HeliosNode node;

	public KtNode() {

	}

	@Override
	public void writeTo(ZintWriterI writer) {
		karma.writeTo(writer);
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		karma = new KarmaDefPack();
		karma.readFrom(reader);
	}

	@Override
	int getId() {
		if (karma == null)
			return 0;
		else
			return karma.core.tid;
	}

	@Override
	public int getIdParent() {
		return karma.core.tidParent;
	}

	@Override
	void setIdParent(int idParent) {
		karma.core.tidParent = idParent;
	}

	public LinkedList<String> getPath() {
		LinkedList<String> path = new LinkedList<String>();
		KtNode pointer = this;
		while (pointer != null && pointer.karma != null) {
			path.addFirst(pointer.karma.core.name);
			pointer = (KtNode) pointer.parent;
		}
		return path;
	}
}
