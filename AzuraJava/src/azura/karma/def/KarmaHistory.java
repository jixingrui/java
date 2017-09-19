package azura.karma.def;

import java.util.LinkedList;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class KarmaHistory implements BytesI {

	// runner
	public LinkedList<KarmaDefV> versionList = new LinkedList<KarmaDefV>();

	// runtime
	private KarmaSpace space;

	public KarmaHistory(KarmaSpace space) {
		this.space = space;
	}

	public KarmaDefV getVersion(int versionField) {
		for (KarmaDefV v : versionList) {
			if (v.version == versionField)
				return v;
		}
		return null;
	}

	public KarmaDefV getHead() {
		if (versionList.isEmpty())
			return null;
		else
			return versionList.getFirst();
	}

	public boolean tryRecord(KarmaDefV current) {
//		versionList.clear();

		KarmaDefV head = getHead();
		if (head == null) {
			versionList.addFirst(current);
			return true;
		}

		if (head.equals(current))
			return false;
		else {
			versionList.addFirst(current);
			return true;
		}

	}

	@Override
	public void fromBytes(byte[] bytes) {
		if (bytes.length == 0)
			return;

		ZintBuffer reader = new ZintBuffer(bytes);
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			KarmaDefV v = new KarmaDefV(space);
			v.readFrom(reader);
			versionList.add(v);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer writer = new ZintBuffer();
		writer.writeZint(versionList.size());
		for (KarmaDefV v : versionList) {
			v.writeTo(writer);
		}
		return writer.toBytes();
	}

}
