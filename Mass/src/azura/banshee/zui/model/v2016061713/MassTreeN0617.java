package azura.banshee.zui.model.v2016061713;

import java.util.ArrayList;

import azura.banshee.zui.model.v2016041415.MassAction0414;
import azura.banshee.zui.model.v2016041415.MassTreeN0414;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class MassTreeN0617 implements ZintCodecI {

	public String name;
	public MassBox0617 box = new MassBox0617();
	public ArrayList<MassAction0617> actionList = new ArrayList<>();

	public MassTree0617 tree;
	public MassTreeN0617 parent;
	public ArrayList<MassTreeN0617> childList = new ArrayList<>();

	public MassTreeN0617(MassTreeN0617 parent) {
		if (parent == null)
			return;
		this.parent = parent;
		this.tree = parent.tree;
	}

	public String toPath() {
		String path = "";
		MassTreeN0617 pointer = this;
		while (pointer != null && pointer.parent != null) {
			String copy = new String(pointer.name);
			copy.replace('.', '_');
			path += copy + ".";
			pointer = pointer.parent;
		}
		return path;
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		name = reader.readUTFZ();
		box.fromBytes(reader.readBytesZ());

		int sizeA = reader.readZint();
		for (int i = 0; i < sizeA; i++) {
			MassAction0617 action = new MassAction0617();
			action.fromBytes(reader.readBytesZ());
			actionList.add(action);
		}

		int sizeC = reader.readZint();
		for (int i = 0; i < sizeC; i++) {
			MassTreeN0617 child = new MassTreeN0617(this);
			child.readFrom(reader);
			childList.add(child);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeUTFZ(name);
		writer.writeBytesZ(box.toBytes());
		writer.writeZint(actionList.size());
		for (MassAction0617 action : actionList) {
			writer.writeBytesZ(action.toBytes());
		}
		writer.writeZint(childList.size());
		for (MassTreeN0617 child : childList) {
			child.writeTo(writer);
		}
	}

	public void eat(MassTreeN0414 old) {
		this.name = old.name;
		this.box.eat(old.box);
		for (MassAction0414 oldAction : old.actionList) {
			MassAction0617 newAction = new MassAction0617();
			actionList.add(newAction);
			newAction.eat(oldAction);
		}
		for (MassTreeN0414 oldChild : old.childList) {
			MassTreeN0617 newChild = new MassTreeN0617(this);
			childList.add(newChild);
			newChild.eat(oldChild);
		}
	}

}
