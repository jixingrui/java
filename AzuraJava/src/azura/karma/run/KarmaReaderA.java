package azura.karma.run;

import java.util.HashMap;

import org.apache.log4j.Logger;

import azura.karma.def.KarmaSpace;
import common.collections.buffer.i.BytesI;

public abstract class KarmaReaderA implements BytesI {
	protected static Logger log = Logger.getLogger(KarmaReaderA.class);

	protected final KarmaSpace space;
	protected final Karma karma;

	private HashMap<String, Runnable> name_function;

	protected KarmaReaderA(KarmaSpace space, int type, int version) {
		this.space = space;
		karma = new Karma(space);
		karma.fromType(type);
		boolean codeMatchDef = space.getDef(type).history.getHead().version == version;
		if (codeMatchDef == false)
			throw new Error();
	}

	protected void addFunction(String name, Runnable function) {
		if (name_function == null)
			name_function = new HashMap<>();
		name_function.put(name, function);
	}

	public void call(String name) {
		if (name_function == null) {
			log.error("function not exist: " + name);
			return;
		}
		Runnable function = name_function.get(name);
		if (function == null) {
			log.error("function not exist: " + name);
			return;
		}
		function.run();
	}

	@Override
	final public void fromBytes(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return;

		try {
			karma.fromBytes(bytes);
			fromKarma(karma);
		} catch (Error e) {
			log.error("decoding error:     " + e.getMessage());
		} finally {
		}
	}

	@Override
	final public byte[] toBytes() {
		return toKarma().toBytes();
	}

	public void fromKarma(Karma karma) {
		throw new Error("to override");
	}

	public Karma toKarma() {
		throw new Error("to override");
	}
}