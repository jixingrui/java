package azura.karma.def;

import azura.karma.def.tree.Tree;

import com.esotericsoftware.kryo.util.IntMap;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.util.FileUtil;

public class KarmaSpace implements BytesI {
	private byte[] zip;

	private IntMap<KarmaNow> type_KarmaNow = new IntMap<KarmaNow>();

	public Tree tree = new Tree(0);

	public KarmaNow getDef(int type) {
		return type_KarmaNow.get(type);
	}

	public void putDef(int type, KarmaNow def) {
		type_KarmaNow.put(type, def);
	}

	public Iterable<KarmaNow> typeIterator() {
		return type_KarmaNow.values();
	}

	// ========= encoding ========
	@Override
	public void fromBytes(byte[] zip) {
		this.zip = zip.clone();
		byte[] bytes = FileUtil.uncompress(zip);

		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			KarmaNow kNow = new KarmaNow(this);
			kNow.readFrom(zb);
			type_KarmaNow.put(kNow.editor.core.tid, kNow);
		}
		tree.fromBytes(zb.readBytesZ());
	}

	@Override
	public byte[] toBytes() {
		if (zip != null)
			return zip.clone();

		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(type_KarmaNow.size);
		for (KarmaNow n : type_KarmaNow.values()) {
			n.writeTo(zb);
		}
		zb.writeBytesZ(tree.toBytes());
		byte[] raw = zb.toBytes();

		zip = FileUtil.compress(raw);
		return zip.clone();
	}

	public KarmaSpace fromFile(String path) {
		byte[] data = FileUtil.read(path);
		fromBytes(data);
		return this;
	}

}
