package azura.banshee.zui.model.v2016041415;

import java.util.ArrayList;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class MassTreeN0414 implements ZintCodecI {

	public String name;
	public MassBox0414 box = new MassBox0414();
	public ArrayList<MassAction0414> actionList = new ArrayList<>();

	public MassTree0414 tree;
	public MassTreeN0414 parent;
	public ArrayList<MassTreeN0414> childList = new ArrayList<>();

	public MassTreeN0414(MassTreeN0414 parent) {
		if (parent == null)
			return;
		this.parent = parent;
		this.tree = parent.tree;
	}

	public String toPath() {
		String path = "";
		MassTreeN0414 pointer = this;
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
		String path = this.toPath();
		if (path != null)
			tree.path_MassTreeN.put(path, this);
		box.fromBytes(reader.readBytesZ());

		int sizeA = reader.readZint();
		for (int i = 0; i < sizeA; i++) {
			MassAction0414 action = new MassAction0414();
			action.fromBytes(reader.readBytesZ());
			actionList.add(action);
		}

		int sizeC = reader.readZint();
		for (int i = 0; i < sizeC; i++) {
			MassTreeN0414 child = new MassTreeN0414(this);
			child.readFrom(reader);
			childList.add(child);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeUTFZ(name);
		writer.writeBytesZ(box.toBytes());
		writer.writeZint(actionList.size());
		for (MassAction0414 action : actionList) {
			writer.writeBytesZ(action.toBytes());
		}
		writer.writeZint(childList.size());
		for (MassTreeN0414 child : childList) {
			child.writeTo(writer);
		}
	}

}
