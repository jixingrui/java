package azura.banshee.zbase.bus;

import java.util.HashSet;

import common.algorithm.FastMath;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class NodeGroup implements BytesI {
	private static int idMax = 0;
	public final int id;
	public final int color;
	HashSet<WayDot45> content = new HashSet<>();

	// public HashMap<Integer, WayDot45> xy_WayDot45 = new HashMap<>();

	public NodeGroup() {
		id = idMax++;
		color = 0x99000000 | FastMath.random(0, 0xffffff);
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(content.size());
		for (WayDot45 n : content) {
			zb.writeBytesZ(n.toBytes());
		}
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			WayDot45 n = new WayDot45();
			n.fromBytes(zb.readBytesZ());
			content.add(n);
			// xy_WayDot45.put(n.xy, n);
		}
		// for (WayDot45 wd : content) {
		// wd.neighborsId.forEach(xy -> {
		// WayDot45 n = xy_WayDot45.get(xy);
		// wd.neighbors.add(n);
		// });
		// wd.neighborsExtId.forEach(xy -> {
		// WayDot45 n = xy_WayDot45.get(xy);
		// wd.neighborsExt.add(n);
		// });
		// }
	}
}
