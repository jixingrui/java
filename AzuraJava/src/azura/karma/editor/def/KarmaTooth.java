package azura.karma.editor.def;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KarmaTooth implements ZintCodecI,BytesI {

	public int targetType;
	public String note = "empty";

	// ==== cache ============
	public String targetName;

	@Override
	public void readFrom(ZintReaderI reader) {
		targetType = reader.readInt();
		note = reader.readUTFZ();
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeInt(targetType);
		writer.writeUTFZ(note);
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		writeTo(zb);
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb=new ZintBuffer(bytes);
		readFrom(zb);
	}

}
