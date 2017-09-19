package azura.karma.def;

import java.util.ArrayList;
import java.util.List;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KarmaDefV implements ZintCodecI {
	public int version;
	public List<KarmaFieldV> fieldList = new ArrayList<KarmaFieldV>();

	// runtime
	private KarmaSpace space;

	public KarmaDefV(KarmaSpace space) {
		this.space = space;
	}

	@Override
	public boolean equals(Object obj) {
		KarmaDefV other = (KarmaDefV) obj;
		if (fieldList.size() != other.fieldList.size())
			return false;

		for (int i = 0; i < fieldList.size(); i++) {
			KarmaFieldV myField = fieldList.get(i);
			KarmaFieldV otherField = other.fieldList.get(i);
			if (myField.equals(otherField) == false)
				return false;
		}

		return true;
	}

	// ======== encoding =========
	@Override
	public void readFrom(ZintReaderI reader) {
		version = reader.readInt();
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			KarmaFieldV field = new KarmaFieldV(space);
			field.readFrom(reader);
			fieldList.add(field);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeInt(version);
		writer.writeZint(fieldList.size());
		for (int i = 0; i < fieldList.size(); i++) {
			KarmaFieldV field = fieldList.get(i);
			field.writeTo(writer);
		}
	}

	// ============ for karma instance =========
	public Bean[] generate() {
		Bean[] beanList = new Bean[fieldList.size()];
		for (int i = 0; i < fieldList.size(); i++) {
			KarmaFieldV field = fieldList.get(i);
			Bean bean = field.newBean();
			beanList[i] = bean;
		}
		return beanList;
	}

//	public void load(ZintBuffer zb, Bean[] beanList) {
//		// Bean[] beanList = generate();
//		for (Bean bean : beanList) {
//			bean.readFrom(zb);
//		}
//		return beanList;
//	}

	public Bean[] update(KarmaDefV old, Bean[] inList) {
		Bean[] result = new Bean[fieldList.size()];
		for (int i = 0; i < fieldList.size(); i++) {
			KarmaFieldV field = fieldList.get(i);
			int idxInOld = old.getFieldIdx(field.tid);
			if (idxInOld == -1) {
				result[i] = field.newBean();
			} else {
				result[i] = inList[idxInOld];
			}
		}
		return result;
	}

	private int getFieldIdx(int tid) {
		for (int i = 0; i < fieldList.size(); i++) {
			KarmaFieldV field = fieldList.get(i);
			if (field.tid == tid)
				return i;
		}
		return -1;
	}

}
