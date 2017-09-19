package common.util;

import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.util.IntMap;

import common.collections.IdRecycle;

public class Session<T> {
	private static Logger log = Logger.getLogger(Session.class);

	private IdRecycle bank;
	private IntMap<Consumer<T>> id_Consumer = new IntMap<Consumer<T>>();

	public Session() {
		bank = new IdRecycle(1);
	}

	synchronized public int plan(Consumer<T> consumer) {
		// log.info("plan");
		int sid = bank.nextId();
		id_Consumer.put(sid, consumer);
		return sid;
	}

	synchronized public void happen(int sid, T resource) {
		// log.info("happen");
		Consumer<T> consumer = id_Consumer.remove(sid);
		if (consumer == null)
			throw new Error();
		consumer.accept(resource);
		bank.recycle(sid);
	}

}
