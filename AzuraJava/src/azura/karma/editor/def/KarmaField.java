package azura.karma.editor.def;

import azura.karma.run.bean.BeanTypeE;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KarmaField implements ZintCodecI, BytesI {

	public int id;
	public BeanTypeE type;
	public String note = "empty";

	// HardItem
	public String name;

	@Override
	public void readFrom(ZintReaderI reader) {
		id = reader.readInt();
		type = BeanTypeE.values()[reader.readZint()];
		note = reader.readUTFZ();
		name = reader.readUTFZ();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeInt(id);
		writer.writeZint(type.ordinal());
		writer.writeUTFZ(note);
		writer.writeUTFZ(name);
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		readFrom(zb);
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		writeTo(zb);
		return zb.toBytes();
	}
}
