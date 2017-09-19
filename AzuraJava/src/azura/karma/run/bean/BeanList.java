package azura.karma.run.bean;

import java.util.List;

import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class BeanList implements BeanI {
	private final KarmaSpace space;
	private final BeanListList list;

	public BeanList(KarmaSpace space, List<Integer> fork) {
		this.space = space;
		list = new BeanListList(fork);
	}

	@Override
	public List<Karma> asList() {
		return list;
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		int size = reader.readZint();
		for (int i = 0; i < size; i++) {
			Karma one = new Karma(space);
			one.fromBytes(reader.readBytesZ());
			list.add(one);
		}
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeZint(list.size());
		for (Karma one : list) {
			writer.writeBytesZ(one.toBytes());
		}
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

	@Override
	public long asLong() {
		throw new IllegalAccessError();
	}

	@Override
	public void setLong(long value) {
		throw new IllegalAccessError();
	}

}
