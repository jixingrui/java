package azura.banshee.zui.model.v2016073113;

import java.util.ArrayList;

import azura.banshee.zui.model.v2016061713.MassAction0617;
import azura.banshee.zui.model.v2016061713.MassTreeN0617;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class MassTreeN0731 implements ZintCodecI {

	public String name;
	public MassBox0731 box = new MassBox0731();
	public ArrayList<MassAction0731> actionList = new ArrayList<>();

	public MassTree0731 tree;
	public MassTreeN0731 parent;
	public ArrayList<MassTreeN0731> childList = new ArrayList<>();

	public MassTreeN0731(MassTreeN0731 parent) {
		if (parent == null)
			return;
		this.parent = parent;
		this.tree = parent.tree;
	}

	public String toPath() {
		String path = "";
		MassTreeN0731 pointer = this;
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
			MassAction0731 action = new MassAction0731();
			action.fromBytes(reader.readBytesZ());
			actionList.add(action);
		}

		int sizeC = reader.readZint();
		for (int i = 0; i < sizeC; i++) {
			MassTreeN0731 child = new MassTreeN0731(this);
			child.readFrom(reader);
			childList.add(child);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeUTFZ(name);
		writer.writeBytesZ(box.toBytes());
		writer.writeZint(actionList.size());
		for (MassAction0731 action : actionList) {
			writer.writeBytesZ(action.toBytes());
		}
		writer.writeZint(childList.size());
		for (MassTreeN0731 child : childList) {
			child.writeTo(writer);
		}
	}

	public void eat(MassTreeN0617 old) {
		this.name = old.name;
		this.box.eat(old.box);
		for (MassAction0617 oldAction : old.actionList) {
			MassAction0731 newAction = new MassAction0731();
			actionList.add(newAction);
			newAction.eat(oldAction);
		}
		for (MassTreeN0617 oldChild : old.childList) {
			MassTreeN0731 newChild = new MassTreeN0731(this);
			childList.add(newChild);
			newChild.eat(oldChild);
		}
	}

}
