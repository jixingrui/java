package azura.expresso.bean;

import azura.expresso.Bean;
import azura.expresso.Datum;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;


public class BeanBytes extends Bean {
	byte[] value;

	@Override
	public void readFrom(ZintReaderI source) {
		value = source.readBytesZ();
	}

	@Override
	public void writeTo(ZintWriterI dest) {
		dest.writeBytesZ(value);
	}

	@Override
	public void eat(Bean pray) {
		value = pray.asBytes();
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
	public byte[] asBytes() {
		return value;
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
	public void setString(String string) {
		throw new IllegalAccessError();
	}

	@Override
	public boolean asBoolean() {
		throw new IllegalAccessError();
	}

	@Override
	public void setBoolean(boolean value) {
		throw new IllegalAccessError();
	}

	@Override
	public void setBytes(byte[] value) {
		this.value = value;
		changed.set(true);
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
