package azura.karma.run.bean;

import java.util.List;

import azura.karma.run.Karma;
import common.collections.buffer.i.ZintCodecI;

public interface BeanI extends ZintCodecI {

	// ===== get =====
	public abstract boolean asBoolean();

	public abstract int asInt();

	public abstract long asLong();

	public abstract double asDouble();

	public abstract String asString();

	public abstract byte[] asBytes();

	public abstract Karma asKarma();

	public abstract List<Karma> asList();

	// ===== set =====
	public abstract void setBoolean(boolean value);

	public abstract void setInt(int value);

	public abstract void setLong(long value);

	public abstract void setDouble(double value);

	public abstract void setString(String value);

	public abstract void setBytes(byte[] value);

	public abstract void setKarma(Karma value);

}
