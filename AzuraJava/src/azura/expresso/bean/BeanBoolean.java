package azura.expresso.bean;

import azura.expresso.Bean;
import azura.expresso.Datum;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;


public class BeanBoolean extends Bean {
	boolean value;

	@Override
	public void readFrom(ZintReaderI source) {
		value=source.readBoolean();
	}

	@Override
	public void writeTo(ZintWriterI dest) {
		dest.writeBoolean(value);
	}

	@Override
	public void eat(Bean pray) {
		value=pray.asBoolean();
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
		return value;
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
		this.value = value;
		changed.set(true);
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
