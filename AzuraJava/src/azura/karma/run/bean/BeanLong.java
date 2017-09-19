package azura.karma.run.bean;

import java.util.List;

import azura.karma.run.Karma;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class BeanLong implements BeanI {
	private long value;

	@Override
	public long asLong() {
		return value;
	}

	@Override
	public void setLong(long value) {
		this.value = value;
	}

	@Override
	public void readFrom(ZintReaderI source) {
		value = source.readLong();
	}

	@Override
	public void writeTo(ZintWriterI dest) {
		dest.writeLong(value);
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
	public Karma asKarma() {
		throw new IllegalAccessError();
	}

	@Override
	public List<Karma> asList() {
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
	public void setKarma(Karma value) {
		throw new IllegalAccessError();
	}

}
