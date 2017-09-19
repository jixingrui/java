package azura.banshee.zbase.bus;

import java.util.ArrayList;
import java.util.HashSet;

import common.algorithm.FastMath;
import common.algorithm.Pack16;
import common.collections.Point;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class WayDot45 implements Comparable<WayDot45>, BytesI {
	public final HashSet<WayDot45> neighbors = new HashSet<>();
	public final HashSet<WayDot45> neighborsExt = new HashSet<>();
	public final ArrayList<Line> lines = new ArrayList<>();
	public int x;
	public int y;
	public GeoType type;
	/**
	 * if cut, not round trip
	 */
	public boolean vital;
	public NodeGroup group;
	public int space;

	// cache
	public int color;
	public int xy;
	public ArrayList<Integer> neighborsId = new ArrayList<>();
	public ArrayList<Integer> neighborsExtId = new ArrayList<>();

	public WayDot45(int x, int y, GeoType type) {
		this.x = x;
		this.y = y;
		this.xy = Pack16.pack(x, y);
		this.type = type;
		color = FastMath.randomBrightColor();
	}

	public WayDot45() {
	}

	public Point getPoint() {
		return new Point(x, y);
	}

	public void assignGroup(NodeGroup group) {
		if (this.group != null)
			return;

		this.group = group;
		group.content.add(this);
		for (WayDot45 n : neighbors) {
			n.assignGroup(group);
		}
	}

	public Line startLine() {
		Line line = new Line(this);
		lines.add(line);
		return line;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		x = zb.readZint();
		y = zb.readZint();
		xy = Pack16.pack(x, y);
		space = zb.readZint();
		type = GeoType.values()[zb.readZint()];
		vital = zb.readBoolean();
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			int xy = zb.readInt();
			neighborsId.add(xy);
		}
		size = zb.readZint();
		for (int i = 0; i < size; i++) {
			int xy = zb.readInt();
			neighborsExtId.add(xy);
		}
	}

	@Override
	public byte[] toBytes() {
		if (neighbors.contains(this))
			throw new Error();

		// List<WayDot45> conn = new ArrayList<>();
		// for (WayDot45 n : neighbors) {
		// conn.add(n);
		// }
		// Logger.getLogger(this.getClass()).info("neighbors = "+conn.size());

		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(x);
		zb.writeZint(y);
		zb.writeZint(space);
		zb.writeZint(type.ordinal());
		zb.writeBoolean(vital);
		// zb.writeZint(group.id);
		zb.writeZint(neighbors.size());
		for (WayDot45 n : neighbors) {
			zb.writeInt(n.xy);
		}
		zb.writeZint(neighborsExt.size());
		for (WayDot45 n : neighborsExt) {
			zb.writeInt(n.xy);
		}
		return zb.toBytes();
	}

	@Override
	public int compareTo(WayDot45 o) {
		if (this.y > o.y)
			return 1;
		else if (this.y < o.y)
			return -1;
		else if (this.x > o.x)
			return 1;
		else if (this.x < o.x)
			return -1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return type.name() + ":" + xy + "(" + x + "," + y + ")";
	}

}
