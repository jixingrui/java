package azura.helios5.join;

import java.util.ArrayList;
import java.util.List;

import azura.helios5.Helios5Core;
import azura.helios5.HeliosNode;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Join implements BytesI {

	public Helios5Core helios;

	public int start;
	public int limit;
	public List<Long> froms = new ArrayList<>();
	public List<Long> tos = new ArrayList<>();

	public Join() {

	}

	public Join(Helios5Core helios) {
		this.helios = helios;
		// this(0, 0);
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

	public Join addTo(HeliosNode to) {
		tos.add(to.id);
		return this;
	}

	public JoinOr addToOr() {
		return new JoinOr();
	}

	public Join addFrom(HeliosNode from) {
		froms.add(from.id);
		return this;
	}

	public JoinOr addFromOr() {
		return new JoinOr();
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
