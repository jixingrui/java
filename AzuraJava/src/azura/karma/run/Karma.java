package azura.karma.run;

import java.util.List;

import org.apache.log4j.Logger;

import azura.karma.def.Bean;
import azura.karma.def.KarmaDefV;
import azura.karma.def.KarmaNow;
import azura.karma.def.KarmaSpace;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Karma implements BytesI {
	private static Logger log = Logger.getLogger(Karma.class);

	private int type;
	private int version;
	private Bean[] beanList;

	// runtime
	private final KarmaSpace space;

	public Karma(KarmaSpace space) {
		this.space = space;
		if (space == null)
			throw new Error();
	}

	public Karma fromType(int type) {
		this.type = type;
		KarmaNow def = space.getDef(type);
		if (def == null)
			throw new Error();
		KarmaDefV headDef = def.history.getHead();
		this.version = headDef.version;
		beanList = headDef.generate();
		return this;
	}

	public int getType() {
		return type;
	}

	public Karma clone() {
		Karma c = new Karma(space);
		c.fromBytes(this.toBytes());
		return c;
	}

	// =========================== encoding ======================

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer reader = new ZintBuffer(bytes);

		int inType = reader.readInt();
		int inVersion = reader.readInt();
		if (inType == 0)
			throw new Error("type not match");
		if (type != 0 && type != inType)
			throw new Error("type not match");

		KarmaNow def = space.getDef(inType);
		if (def == null) {// type not exist
			log.error("type not exist");
			return;
		}

		KarmaDefV headDef = def.history.getHead();
		if (this.type == 0)
			beanList = headDef.generate();
		this.type = inType;

		if (headDef.version == inVersion) {// version is head
			this.version = inVersion;
			load(reader, beanList);
			return;
		}

		KarmaDefV oldDef = def.history.getVersion(inVersion);
		if (oldDef == null) {// version not exist
			this.version = headDef.version;
			beanList = headDef.generate();
			log.error("old version not exist: type=" + type + " version=" + version);
			return;
		}

		// update version
		Bean[] oldList = oldDef.generate();
		load(reader, oldList);
		beanList = headDef.update(oldDef, oldList);
		this.version = headDef.version;
//		log.info("updating: type=" + type + "from version=" + inVersion + " to version=" + version);
	}

	private void load(ZintBuffer reader, Bean[] bl) {
		for (Bean bean : bl) {
			bean.readFrom(reader);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer writer = new ZintBuffer();
		writer.writeInt(type);
		writer.writeInt(version);
		// writer.writeZint(beanList.length);
		for (Bean bean : beanList) {
			bean.writeTo(writer);
		}
		return writer.toBytes();
	}

	// =========================== getter =====================

	private Bean getBean(int idx) {
		return beanList[idx];
	}

	public boolean getBoolean(int idx) {
		return getBean(idx).asBoolean();
	}

	public int getInt(int idx) {
		return getBean(idx).asInt();
	}

	public double getDouble(int idx) {
		return getBean(idx).asDouble();
	}

	public String getString(int idx) {
		return getBean(idx).asString();
	}

	public byte[] getBytes(int idx) {
		return getBean(idx).asBytes();
	}

	public Karma getKarma(int idx) {
		return getBean(idx).asKarma();
	}

	public List<Karma> getList(int idx) {
		return getBean(idx).asList();
	}

	// ============================ setter ====================
	public void setBoolean(int idx, boolean value) {
		getBean(idx).setBoolean(value);
	}

	public void setInt(int idx, int value) {
		getBean(idx).setInt(value);
	}

	public void setDouble(int idx, double value) {
		getBean(idx).setDouble(value);
	}

	public void setString(int idx, String value) {
		getBean(idx).setString(value);
	}

	public void setBytes(int idx, byte[] value) {
		getBean(idx).setBytes(value);
	}

	public void setKarma(int idx, Karma value) {
		if (value == this)
			throw new Error();
		getBean(idx).setKarma(value);
	}

	// ============================ mod ====================
	/**
	 * @return new value
	 */
	public int addInt(int idx, int delta) {
		Bean b = getBean(idx);
		int value = b.asInt();
		value += delta;
		b.setInt(value);
		return value;
	}

	/**
	 * @return new value
	 */
	public double addDouble(int idx, double delta) {
		Bean b = getBean(idx);
		double value = b.asDouble();
		value += delta;
		b.setDouble(value);
		return value;
	}

}
