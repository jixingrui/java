package azura.helios6.read;

import java.util.ArrayList;
import java.util.List;

import azura.helios6.Helios6Core;
import azura.helios6.Hnode;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Join implements BytesI {

	public Helios6Core helios;

	public int start;
	public int limit;
	public List<Long> froms = new ArrayList<>();
	public List<Long> tos = new ArrayList<>();

	public Join() {

	}

	public Join(Helios6Core helios) {
		this.helios = helios;
	}

	/**
	 * @param start
	 *            must be non negative, 0 means the first
	 * @param limit
	 *            must be non negative, 0 means all
	 */
	public Join(int start, int limit) {
		if (start < 0 || limit < 0)
			throw new IllegalArgumentException(
					"Join: start and limit must be non negative");
		this.start = start;
		this.limit = limit;
	}

	public JoinList run() {
		return helios.join(this);
	}

	public JoinList run(int start, int limit) {
		this.start = start;
		this.limit = limit;
		return helios.join(this);
	}

	public Join addTo(Hnode to) {
		tos.add(to.getId());
		return this;
	}

	public Join addFrom(Hnode from) {
		froms.add(from.getId());
		return this;
	}

	@Override
	public void fromBytes(byte[] data) {
		ZintBuffer zb = new ZintBuffer(data);
		start = zb.readZint();
		limit = zb.readZint();
		int fromSize = zb.readZint();
		for (int i = 0; i < fromSize; i++) {
			froms.add(zb.readLong());
		}
		int toSize = zb.readZint();
		for (int i = 0; i < toSize; i++) {
			tos.add(zb.readLong());
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(start);
		zb.writeZint(limit);
		zb.writeZint(froms.size());
		for (Long from : froms)
			zb.writeLong(from);
		zb.writeZint(tos.size());
		for (Long to : tos)
			zb.writeLong(to);
		return zb.toBytes();
	}

}
