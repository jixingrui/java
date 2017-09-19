package azura.expresso;

import java.util.ArrayList;
import java.util.List;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class DeltaOld {
//	public final int oid;
	public final Clazz clazz;
	List<Change> changeList = new ArrayList<Change>();

	public DeltaOld(Clazz clazz) {
//		this.oid = oid;
		this.clazz = clazz;
	}

	public DeltaOld(NameSpace ns, byte[] data) {
		ZintReaderI zb = new ZintBuffer(data);
//		oid = zb.readZint();
		int idClass = zb.readZint();
		clazz = ns.getClass(idClass);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			int idx = zb.readZint();
			Bean bean = clazz.fieldAll.get(idx).newBean();
			bean.readFrom(zb);
			changeList.add(new Change(idx, bean));
		}
	}

	public void encode(ZintWriterI dest) {
//		dest.writeZint(oid);
		dest.writeZint(clazz.id);
		dest.writeZint(changeList.size());
		for (Change c : changeList) {
			dest.writeZint(c.idx);
			c.bean.writeTo(dest);
		}
	}

	public void add(int idx, Bean bean) {
		changeList.add(new Change(idx, bean));
	}

	public void apply(Datum datum) {
		for (Change c : changeList) {
			datum.beanList[c.idx].change(c.bean);
		}
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		encode(zb);
		return zb.toBytes();
	}

	@Override
	public String toString() {
		return "delta: " + 0 + "_" + clazz.id + "_" + changeList.size();
	}
}

class Change {
	int idx;
	Bean bean;

	public Change(int idx, Bean bean) {
		this.idx = idx;
		this.bean = bean;
	}
}