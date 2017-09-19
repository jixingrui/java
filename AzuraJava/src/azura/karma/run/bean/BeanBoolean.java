package azura.karma.run.bean;

import java.util.List;

import azura.karma.run.Karma;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class BeanBoolean implements BeanI {
	boolean value;

	@Override
	public boolean asBoolean() {
		return value;
	}

	@Override
	public void setBoolean(boolean value) {
		this.value = value;
	}

	@Override
	public void readFrom(ZintReaderI source) {
		value = source.readBoolean();
	}

	@Override
	public void writeTo(ZintWriterI dest) {
		dest.writeBoolean(value);
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

	@Override
	public long asLong() {
		throw new IllegalAccessError();
	}

	@Override
	public void setLong(long value) {
		throw new IllegalAccessError();
	}

}
