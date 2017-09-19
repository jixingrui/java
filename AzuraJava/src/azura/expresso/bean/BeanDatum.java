package azura.expresso.bean;

import azura.expresso.Bean;
import azura.expresso.Datum;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class BeanDatum extends Bean {
	private int type;
	private Datum value;

	public BeanDatum(int type, Datum value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public void readFrom(ZintReaderI source) {
		value.readFrom(source);
	}

	@Override
	public void writeTo(ZintWriterI dest) {
		value.writeTo(dest);
	}

	@Override
	public void eat(Bean pray) {
		value = pray.asDatum();
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
		return value;
	}

	@Override
	public BeanListI asList() {
		throw new IllegalAccessError();
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
		this.value = value;
		changed.set(true);
		if (value.CLASSID() != type)
			throw new IllegalArgumentException();
	}

}
