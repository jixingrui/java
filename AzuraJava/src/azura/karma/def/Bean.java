package azura.karma.def;

import java.util.List;

import azura.karma.run.Karma;
import azura.karma.run.bean.BeanBoolean;
import azura.karma.run.bean.BeanBytes;
import azura.karma.run.bean.BeanDouble;
import azura.karma.run.bean.BeanI;
import azura.karma.run.bean.BeanInt;
import azura.karma.run.bean.BeanKarma;
import azura.karma.run.bean.BeanList;
import azura.karma.run.bean.BeanLong;
import azura.karma.run.bean.BeanString;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Bean implements BeanI {

	private final BeanI data;

	Bean(KarmaFieldV field) {
		this.data = typeToData(field);
	}

	private BeanI typeToData(KarmaFieldV field) {
		BeanI data = null;
		switch (field.type) {
		case BOOLEAN: {
			data = new BeanBoolean();
			break;
		}
		case INT: {
			data = new BeanInt();
			break;
		}
		case Long: {
			data = new BeanLong();
			break;
		}
		case DOUBLE: {
			data = new BeanDouble();
			break;
		}
		case STRING: {
			data = new BeanString();
			break;
		}
		case BYTES: {
			data = new BeanBytes();
			break;
		}
		case KARMA: {
			data = new BeanKarma(field.space, field.fork);
			break;
		}
		case LIST: {
			data = new BeanList(field.space, field.fork);
			break;
		}
		case EMPTY: {
			data = null;
			break;
		}
		}
		return data;
	}

	@Override
	public void readFrom(ZintReaderI reader) {
		data.readFrom(reader);
	}

	@Override
	public void writeTo(ZintWriterI writer) {
		data.writeTo(writer);
	}

	// ============ get ===========
	@Override
	public int asInt() {
		return data.asInt();
	}

	@Override
	public long asLong() {
		return data.asLong();
	}

	@Override
	public String asString() {
		return data.asString();
	}

	@Override
	public boolean asBoolean() {
		return data.asBoolean();
	}

	@Override
	public byte[] asBytes() {
		return data.asBytes();
	}

	@Override
	public double asDouble() {
		return data.asDouble();
	}

	@Override
	public Karma asKarma() {
		return data.asKarma();
	}

	@Override
	public List<Karma> asList() {
		return data.asList();
	}

	// =========== set ===========
	@Override
	public void setInt(int value) {
		data.setInt(value);
	}

	@Override
	public void setLong(long value) {
		data.setLong(value);
	}

	@Override
	public void setString(String value) {
		data.setString(value);
	}

	@Override
	public void setBoolean(boolean value) {
		data.setBoolean(value);
	}

	@Override
	public void setBytes(byte[] value) {
		data.setBytes(value);
	}

	@Override
	public void setDouble(double value) {
		data.setDouble(value);
	}

	@Override
	public void setKarma(Karma value) {
		data.setKarma(value);
	}

}
