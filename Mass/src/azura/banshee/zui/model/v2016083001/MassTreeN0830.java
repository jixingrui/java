package azura.banshee.zui.model.v2016083001;

import java.util.ArrayList;

import azura.banshee.zui.model.v2016073113.MassAction0731;
import azura.banshee.zui.model.v2016073113.MassTreeN0731;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class MassTreeN0830 implements ZintCodecI {

	public String name;
	public MassBox0830 box = new MassBox0830();
	public ArrayList<MassAction0830> actionList = new ArrayList<>();

	public MassTree0830 tree;
	public MassTreeN0830 parent;
	public ArrayList<MassTreeN0830> childList = new ArrayList<>();

	public MassTreeN0830(MassTreeN0830 parent) {
		if (parent == null)
			return;
		this.parent = parent;
		this.tree = parent.tree;
	}

	public String toPath() {
		String path = "";
		MassTreeN0830 pointer = this;
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
			MassAction0830 action = new MassAction0830();
			action.fromBytes(reader.readBytesZ());
			actionList.add(action);
		}

		int sizeC = reader.readZint();
		for (int i = 0; i < sizeC; i++) {
			MassTreeN0830 child = new MassTreeN0830(this);
			child.readFrom(reader);
			childList.add(child);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeUTFZ(name);
		writer.writeBytesZ(box.toBytes());
		writer.writeZint(actionList.size());
		for (MassAction0830 action : actionList) {
			writer.writeBytesZ(action.toBytes());
		}
		writer.writeZint(childList.size());
		for (MassTreeN0830 child : childList) {
			child.writeTo(writer);
		}
	}

	public void eat(MassTreeN0731 old) {
		this.name = old.name;
		this.box.eat(old.box);
		for (MassAction0731 oldAction : old.actionList) {
			MassAction0830 newAction = new MassAction0830();
			actionList.add(newAction);
			newAction.eat(oldAction);
		}
		for (MassTreeN0731 oldChild : old.childList) {
			MassTreeN0830 newChild = new MassTreeN0830(this);
			childList.add(newChild);
			newChild.eat(oldChild);
		}
	}

}
