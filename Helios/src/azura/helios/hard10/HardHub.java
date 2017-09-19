package azura.helios.hard10;

import java.util.EnumMap;

import org.apache.log4j.Logger;

import azura.expresso.rpc.phoenix13.TunnelI;
import azura.helios5.Helios5;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;

public class HardHub<E extends Enum<E>> {
	static Logger log = Logger.getLogger(HardHub.class);

	public final Helios5<?> helios;
	private final TunnelI tunnel;
	private final EnumMap<E, HardHandlerA> enum_Hard;

	private final E[] enumList;

	public HardHub(Helios5<?> helios, TunnelI tunnel, Class<E> c) {
		this.helios = helios;
		this.tunnel = tunnel;
		this.enumList = c.getEnumConstants();

		enum_Hard = new EnumMap<E, HardHandlerA>(c);
	}

	public void register(E key, HardHandlerA value) {
		value.id = key.ordinal();
		value.hub=this;
		enum_Hard.put(key, value);
	}

	public HardHandlerA get(E key) {
		return enum_Hard.get(key);
	}

	public HardHandlerA get(int id) {
		if (id < 0 || id >= enumList.length)
			return null;
		return get(enumList[id]);
	}

	public void receive(byte[] msg) {
		int id;
		byte[] data;
		try {
			ZintReaderI zb = new ZintBuffer(msg);
			id = zb.readZint();
			data = zb.readBytesZ();
		} catch (Exception e) {
			log.error("decode error");
			return;
		}

		HardHandlerA handler = get(id);
		if (handler != null)
			handler.receive(data);
		else
			log.error("hard not exist: " + id);
	}

	void send(int id, byte[] msg) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(id);
		zb.writeBytesZ(msg);
		tunnel.tunnelOut(zb);
	}
}
