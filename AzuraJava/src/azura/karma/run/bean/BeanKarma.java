package azura.karma.run.bean;

import java.util.List;

import org.apache.log4j.Logger;

import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;

import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class BeanKarma implements BeanI {
	private final Logger log = Logger.getLogger(BeanKarma.class);

	private final KarmaSpace space;
	private final List<Integer> fork;
	private Karma value;

	public BeanKarma(KarmaSpace space, List<Integer> fork) {
		this.space = space;
		this.fork = fork;
	}

	@Override
	public Karma asKarma() {
		return value;
	}

	@Override
	public void setKarma(Karma value) {
		if (fork.contains(value.getType()))
			this.value = value;
		else
			throw new Error();
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		boolean valueIsNull = reader.readBoolean();
		if (valueIsNull == false) {
			Karma in = new Karma(space);
			in.fromBytes(reader.readBytesZ());
			setKarma(in);
		} else
			log.warn("read: karma is null");
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		writer.writeBoolean(value == null);
		if (value != null)
			writer.writeBytesZ(value.toBytes());
		else
			log.warn("write: karma is null");
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
	public long asLong() {
		throw new IllegalAccessError();
	}

	@Override
	public void setLong(long value) {
		throw new IllegalAccessError();
	}

}
