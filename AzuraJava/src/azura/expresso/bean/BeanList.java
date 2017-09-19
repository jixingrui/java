package azura.expresso.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import azura.expresso.Bean;
import azura.expresso.Datum;
import azura.expresso.field.FieldA;
import common.collections.buffer.i.ZintCodecI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class BeanList extends Bean implements BeanListI {

	private FieldA type;
	private List<Bean> value = new ArrayList<Bean>();

	public BeanList(FieldA type) {
		this.type = type;
	}

	public Bean addBean() {
		Bean b = type.newBean();
		value.add(b);
		return b;
	}

	@Override
	public Iterator<Bean> iterator() {
		return value.iterator();
	}

	//	@Override
	//	List<Bean> getList() {
	//		return value;
	//	}

	@Override
	public void readFrom(ZintReaderI source) {
		int size = source.readZint();
		value = new ArrayList<Bean>(size);
		for (int i = 0; i < size; i++) {
			Bean item = type.newBean();
			item.readFrom(source);
			value.add(item);
		}
	}

	@Override
	public void writeTo(ZintWriterI dest) {
		dest.writeZint(value.size());
		for (ZintCodecI b : value) {
			b.writeTo(dest);
		}
	}

	@Override
	public void eat(Bean pray) {
		BeanList cast=(BeanList) pray.asList();
		if(cast!=null)
			value = cast.value;
	}

	@Override
	public int asInt() {
		throw new IllegalAccessError();
	}

	@Override
	public String asString() {
		throw new IllegalAccessError();
	}

	@Override
	public boolean asBoolean() {
		throw new IllegalAccessError();
	}

	@Override
	public byte[] asBytes() {
		throw new IllegalAccessError();
	}

	@Override
	public double asDouble() {
		throw new IllegalAccessError();
	}

	@Override
	public Datum asDatum() {
		throw new IllegalAccessError();
	}

	@Override
	public BeanList asList() {
		return this;
	}

	@Override
	public void setInt(int value) {
		throw new IllegalAccessError();
	}

	@Override
	public void setString(String value) {
		throw new IllegalAccessError();
	}

	@Override
	public void setBoolean(boolean value) {
		throw new IllegalAccessError();
	}

	@Override
	public void setBytes(byte[] value) {
		throw new IllegalAccessError();
	}

	@Override
	public void setDouble(double value) {
		throw new IllegalAccessError();
	}

	@Override
	public void setDatum(Datum value) {
		throw new IllegalAccessError();
	}

}
