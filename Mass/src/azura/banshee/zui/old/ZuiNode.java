package azura.banshee.zui.old;

import java.util.List;

import common.collections.ArrayListAuto;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class ZuiNode implements ZintCodecI {

	public ZuiType type = ZuiType.Container;
	public ZintCodecI branch;
	List<ZuiNode> childList = new ArrayListAuto<ZuiNode>();

	@Override
	public void readFrom(ZintReaderI reader) {
		type = ZuiType.values()[reader.readInt()];
		switch (type) {
		case State: {
			branch = new State();
			break;
		}
		case Container: {
			branch = new Container();
			break;
		}
		case Image: {
			branch = new Image();
			break;
		}
		}
		branch.readFrom(reader);
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			ZuiNode zn = new ZuiNode();
			childList.add(zn);
			zn.readFrom(reader);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(type.ordinal());
		branch.writeTo(writer);
		writer.writeZint(childList.size());
		for (int i = 0; i < childList.size(); i++) {
			childList.get(i).writeTo(writer);
		}
	}

	public State asState() {
		if (type != ZuiType.State)
			throw new Error();

		return (State) branch;
	}

	public Container asContainer() {
		if (type != ZuiType.Container)
			throw new Error();

		return (Container) branch;
	}

	public Image asImage() {
		if (type != ZuiType.Image)
			throw new Error();

		return (Image) branch;
	}

}
