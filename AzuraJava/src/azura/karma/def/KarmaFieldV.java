package azura.karma.def;

import java.util.ArrayList;

import azura.karma.run.bean.BeanTypeE;

import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class KarmaFieldV implements ZintCodecI {
	public int tid;
	public BeanTypeE type;
	public ArrayList<Integer> fork;// Karma type

	// ====== runtime ======
	KarmaSpace space;

	KarmaFieldV(KarmaSpace space) {
		this.space = space;
	}

	Bean newBean() {
		return new Bean(this);
	}

	@Override
	public boolean equals(Object obj) {
		KarmaFieldV other = (KarmaFieldV) obj;
		if (tid != other.tid)
			return false;
		if (type != other.type)
			return false;
		if (fork == null && other.fork == null)
			return true;
		if (fork != null && other.fork != null) {
			return fork.equals(other.fork);
		} else
			return false;
	}

	// =================== encoding ===============
	@Override
	public void readFrom(ZintReaderI reader) {
		tid = reader.readInt();
		type = BeanTypeE.values()[reader.readZint()];
		if (type == BeanTypeE.KARMA || type == BeanTypeE.LIST) {
			int size = reader.readZint();
			fork = new ArrayList<Integer>(size);
			for (int i = 0; i < size; i++) {
				fork.add(reader.readInt());
			}
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeInt(tid);
		writer.writeZint(type.ordinal());
		if (type == BeanTypeE.KARMA || type == BeanTypeE.LIST) {
			writer.writeZint(fork.size());
			for (int i = 0; i < fork.size(); i++) {
				writer.writeInt(fork.get(i));
			}
		}
	}
}
