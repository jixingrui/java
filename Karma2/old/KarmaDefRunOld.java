package azura.karma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import common.collections.StringMap;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KarmaDefRunOld implements ZintCodecI {
	private static Logger log = Logger.getLogger(KarmaDefRunOld.class);

	// data
	public KarmaDefExt def;

	// runtime
	KarmaSpace space;
	StringMap<Integer> field_idx = new StringMap<Integer>();
	List<KarmaField> idx_Field = new ArrayList<KarmaField>();

	StringMap<Integer> type_id = new StringMap<Integer>();

	public int typeToId(String type) {
		return type_id.get(type);
	}

	public int fieldToIdx(String field) {
		return field_idx.get(field);
	}

	// ======================= version ===================

	public Karma newKarma() {
		Karma result = new Karma(space);
		// result.myDef = this;
		result.type = def.id;
		result.version = def.version;
		for (Entry<Integer, KarmaField> e : def.id_Field.entrySet()) {
			Bean bean = new Bean(space, e.getValue().type);
			result.fieldList.add(bean);
		}
		return result;
	}

	public Karma update(Karma oldK) {
		if (oldK.version == def.version)
			return oldK;

		if (oldK.version > def.version) {
			log.error("version cannot degrade: from " + oldK.version + " to "
					+ def.version);
			throw new Error();
		}

		Karma newK = newKarma();
		for (Entry<Integer, KarmaField> newF : def.id_Field.entrySet()) {
			Bean nb = newK.getBean(newF.getValue().idx);
			KarmaField oldField = oldK.myDef.id_Field.get(newF.getKey());
			if (oldField == null)
				continue;

			Bean ob = oldK.getBean(oldField.idx);
			nb.eat(ob);
		}

		return newK;
	}

	// ======================= encoding ====================
	@Override
	public void readFrom(ZintReaderI reader) {
		// super.readFrom(reader);

		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			type_id.put(reader.readUTFZ(), reader.readInt());
		}

		int idx = 0;
		for (KarmaField kf : def.id_Field.values()) {
			kf.idx = idx;
			idx++;
			field_idx.put(kf.name, kf.idx);
			idx_Field.add(kf);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		// super.writeTo(writer);

		writer.writeZint(type_id.size());
		for (Entry<String, Integer> e : type_id.entrySet()) {
			writer.writeUTFZ(e.getKey());
			writer.writeInt(e.getValue());
		}
	}
}
