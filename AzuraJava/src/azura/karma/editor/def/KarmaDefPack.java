package azura.karma.editor.def;

import common.collections.ArrayListAuto;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class KarmaDefPack implements BytesI {

	public KarmaDef core = new KarmaDef();
	public ArrayListAuto<KarmaFieldPack> fieldList = new ArrayListAuto<KarmaFieldPack>();

	// cache
	// public HeliosNode node;
	// public HardItem hi;

	public KarmaDefPack() {

	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer reader = new ZintBuffer(bytes);
		core.fromBytes(reader.readBytesZ());

		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			KarmaFieldPack kf = new KarmaFieldPack();
			kf.readFrom(reader);
			fieldList.add(kf);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer writer = new ZintBuffer();
		writer.writeBytesZ(core.toBytes());

		writer.writeZint(fieldList.size());
		for (KarmaFieldPack f : fieldList) {
			f.writeTo(writer);
		}
		return writer.toBytes();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(core.name).append(": ");
		for (KarmaFieldPack field : fieldList) {
			sb.append(field.core.name).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
