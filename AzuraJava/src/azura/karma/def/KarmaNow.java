package azura.karma.def;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import azura.karma.def.tree.TreeNode;
import azura.karma.editor.def.KarmaDefPack;
import azura.karma.editor.def.KarmaFieldPack;
import azura.karma.editor.def.KarmaTooth;
import azura.karma.run.bean.BeanTypeE;
import common.algorithm.FastMath;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KarmaNow implements ZintCodecI {
	private static Logger log = Logger.getLogger(KarmaNow.class);

	public KarmaDefPack editor;
	public KarmaHistory history;
	private KarmaSpace space;

	public KarmaNow(KarmaSpace space) {
		this.space = space;
		editor = new KarmaDefPack();
	}

	public boolean isTop() {
		return space.tree.getNode(editor.core.tid).getParent() == space.tree.getRoot();
	}

	public KarmaNow getParent() {
		TreeNode ptn = space.tree.getNode(editor.core.tid).getParent();
		return space.getDef(ptn.id);
	}

	public String getPathString() {
		LinkedList<String> path = new LinkedList<String>();
		TreeNode pointer = space.tree.getNode(editor.core.tid).getParent();
		while (pointer.id != 0) {
			KarmaNow kNode = space.getDef(pointer.id);
			path.addFirst(kNode.editor.core.name);
			pointer = pointer.getParent();
		}
		StringBuilder sb = new StringBuilder();
		for (String piece : path) {
			sb.append("." + piece);
		}
		return sb.toString();
	}

	public LinkedList<String> getPath() {
		LinkedList<String> path = new LinkedList<String>();
		TreeNode pointer = space.tree.getNode(editor.core.tid);
		while (pointer.id != 0) {
			KarmaNow kNode = space.getDef(pointer.id);
			path.addFirst(kNode.editor.core.name);
			pointer = pointer.getParent();
		}
		return path;
	}

	// ================ encoding ===========
	@Override
	public void readFrom(ZintReaderI reader) {
		editor.fromBytes(reader.readBytesZ());
		history = new KarmaHistory(space);
		history.fromBytes(editor.core.historydata);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeBytesZ(editor.toBytes());
	}

	public boolean fromEditor(KarmaDefPack editor) {
		this.editor = editor;
		history = new KarmaHistory(space);
		history.fromBytes(editor.core.historydata);

		KarmaDefV current = new KarmaDefV(space);
		for (KarmaFieldPack field : editor.fieldList) {
			KarmaFieldV fv = new KarmaFieldV(space);
			current.fieldList.add(fv);

			fv.tid = field.core.id;
			fv.type = field.core.type;
			if (fv.type == BeanTypeE.KARMA || fv.type == BeanTypeE.LIST) {
				fv.fork = new ArrayList<Integer>();
				for (KarmaTooth tooth : field.fork) {
					fv.fork.add(tooth.targetType);
				}
			}
		}

		boolean recorded = history.tryRecord(current);
		if (recorded) {
			current.version = FastMath.tidInt();
//			log.debug("new version inserted: " + current.version);
			return true;
		} else {
			return false;
		}
	}

}
