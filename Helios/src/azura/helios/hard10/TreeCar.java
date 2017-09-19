package azura.helios.hard10;

import java.util.ArrayList;
import java.util.List;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class TreeCar implements ZintCodecI {
	public int idCar;
	public int parent;
	public List<Integer> childList = new ArrayList<>();
	public byte[] data;

	@Override
	public void readFrom(ZintReaderI reader) {
		childList.clear();
		idCar = reader.readZint();
		parent = reader.readZint();
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			childList.add(reader.readZint());
		}
		data = reader.readBytesZ();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(idCar);
		writer.writeZint(parent);
		writer.writeZint(childList.size());
		for (int child : childList) {
			writer.writeZint(child);
		}
		writer.writeBytesZ(data);
	}
}
