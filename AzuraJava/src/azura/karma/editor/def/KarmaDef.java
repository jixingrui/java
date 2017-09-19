package azura.karma.editor.def;

import org.apache.log4j.Logger;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class KarmaDef implements BytesI {
	static Logger log = Logger.getLogger(KarmaDef.class);

	public int tid;
	public int versionSelf;
	public String note = "empty";

	// HardItem
	public String name;
	public int tidParent;

	// run
	public byte[] historydata;

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer reader = new ZintBuffer(bytes);
		tid = reader.readInt();
		versionSelf = reader.readInt();
		note = reader.readUTFZ();
		name = reader.readUTFZ();
		tidParent = reader.readInt();
		historydata = reader.readBytesZ();
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer writer = new ZintBuffer();
		writer.writeInt(tid);
		writer.writeInt(versionSelf);
		writer.writeUTFZ(note);
		writer.writeUTFZ(name);
		writer.writeInt(tidParent);
		writer.writeBytesZ(historydata);
		return writer.toBytes();
	}

	@Override
	public boolean equals(Object obj) {
		KarmaDef other = (KarmaDef) obj;
		return this.tid == other.tid;
	}

	@Override
	public int hashCode() {
		return tid;
	}
}
