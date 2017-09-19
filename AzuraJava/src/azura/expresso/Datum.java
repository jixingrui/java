package azura.expresso;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Datum implements ZintCodecI {
	// public int oid = -1;
	Clazz clazz;
	Bean[] beanList;

	Datum(Clazz clazz) {
		this.clazz = clazz;
	}

	// @Override
	// public int getId() {
	// return oid;
	// }
	//
	// @Override
	// public void setId(int value) {
	// oid=value;
	// }

	public Bean getBean(int idField) {
		int idx = clazz.fieldToIdxBean(idField);
		return beanList[idx];
	}

	public boolean isInstanceOf(int idEpo) {
		return clazz.isInstanceOf(idEpo);
	}

	public void writeTo(ZintWriterI writer) {
		for (ZintCodecI b : beanList) {
			b.writeTo(writer);
		}
	}

	// public Datum decode(byte[] source) {
	// if (source == null || source.length == 0)
	// return this;
	// else
	// return decode(new ZintCombo(source));
	// }

	public void readFrom(ZintReaderI reader) {
		for (ZintCodecI b : beanList) {
			b.readFrom(reader);
		}
	}

	public int CLASSID() {
		return clazz.id;
	}

	public byte[] toBytes() {
		ZintWriterI zb = new ZintBuffer();
		this.writeTo(zb);
		return zb.toBytes();
	}

	public void eat(Datum food) {
		Clazz.eat(this, food);
	}

	public DeltaOld extractDelta() {
		DeltaOld delta = new DeltaOld(clazz);
		for (int i = 0; i < this.beanList.length; i++) {
			Bean b = this.beanList[i];
			if (b.changed.getAndSet(false)) {
				delta.add(i, b);
			}
		}
		return delta;
	}

	public void applyDelta(DeltaOld delta) {
		delta.apply(this);
	}

	// public void dispose() {
	// clazz.ns.dispose(this);
	// }
}
