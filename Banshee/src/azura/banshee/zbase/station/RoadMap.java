package azura.banshee.zbase.station;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import azura.avalon.path.BhStrider;
import azura.banshee.zbase.bus.BusMap;
import azura.banshee.zbase.bus.GeoType;
import azura.banshee.zbase.bus.SunLight;
import azura.banshee.zbase.bus.WayDot45;
import common.algorithm.FastMath;
import common.collections.Point;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class RoadMap implements BytesI {
	public static Logger log = Logger.getLogger(RoadMap.class);

	public ZbaseR zbase = new ZbaseR();
	public BusMap busMap = new BusMap();

	public void load(ZbaseR zbase) throws IOException {
		this.zbase = zbase;
		busMap.loadFromZbaseR(zbase);
		doGravity();
	}

	// ======================== use ========================
	public boolean isRoad(int x, int y) {
		return zbase.isRoad(x >> zbase.shrinkZ, y >> zbase.shrinkZ, zbase.zMax);
	}

	public Queue<Point> escape(int xRunner, int yRunner, int xMonster, int yMonster) {
		WayDot45 closer = getStation(xRunner, yRunner);
		// xRunner >>= zbase.shrinkZ;
		// yRunner >>= zbase.shrinkZ;
		xMonster >>= zbase.shrinkZ;
		yMonster >>= zbase.shrinkZ;

		// int weightMax = (int) (FastMath.dist(xMonster, yMonster, closer.x,
		// closer.y)
		// - FastMath.dist(xRunner, yRunner, closer.x, closer.y));

		WayDot45 start = closer;
		int weightMax = -9999;
		for (WayDot45 n : closer.neighbors) {
			int weight = (int) (FastMath.dist(xMonster, yMonster, n.x, n.y)
					- FastMath.dist(xRunner >> zbase.shrinkZ, yRunner >> zbase.shrinkZ, n.x, n.y));
			if (weight > weightMax) {
//				log.debug("start changed");
				start = n;
				weightMax = weight;
			}
		}
		// LinkedList<Point> result = busMap.escape(start, new Point(xMonster,
		// yMonster));

		LinkedList<Point> result = new LinkedList<>();
		result.add(start.getPoint());

		int tail = FastMath.pow2x(zbase.shrinkZ - 1);
		result.forEach(p -> {
			p.x = (p.x << zbase.shrinkZ) + tail;
			p.y = (p.y << zbase.shrinkZ) + tail;
		});

		// if (result.size() > 1) {
		// Point second = result.get(1);
		// if (canSeeC(xRunner >> zbase.shrinkZ, yRunner >> zbase.shrinkZ,
		// second.x >> zbase.shrinkZ,
		// second.y >> zbase.shrinkZ)) {
		// result.removeFirst();
		// }
		// }

		return result;
	}

	public Point getStationPos(int x, int y) {
		WayDot45 station = getStation(x, y);
		Point p = new Point();
		p.x = station.x << zbase.shrinkZ;
		p.y = station.y << zbase.shrinkZ;
		return p;
	}

	public WayDot45 getStation(int x, int y) {
		int xy = zbase.getStation(x, y);
		return busMap.getStation(xy);
	}

	public void doGravity() throws IOException {

		for (WayDot45 dot : busMap.xy_Node.values()) {
			if (dot.type == GeoType.Skeleton)
				continue;

			SunLight rc = new SunLight(dot.x, dot.y);
			Collection<Point> round = rc.nextRound();
			while (round.isEmpty() == false) {
				aurora(round, dot);
				round = rc.nextRound();
			}
		}

	}

	private int aurora(Collection<Point> round, WayDot45 dot) {
		int hitDist = Integer.MAX_VALUE;
		Iterator<Point> it = round.iterator();
		while (it.hasNext()) {
			Point p = it.next();
			int dist = (int) FastMath.dist(p.x, p.y, dot.x, dot.y);
			if (canSeeC(p.x, p.y, dot.x, dot.y)) {
				Pixel pix = zbase.getPixel(p.x, p.y);
				if (dist < pix.gravityValue) {
					pix.gravityValue = dist;
					pix.nearestStation = dot;
				}
			} else {
				it.remove();
				hitDist = Math.min(dist, hitDist);
			}
		}
		return hitDist;
	}

	public boolean canSee(int xStart, int yStart, int xEnd, int yEnd) {
		return canSeeC(xStart >> zbase.shrinkZ, yStart >> zbase.shrinkZ, xEnd >> zbase.shrinkZ, yEnd >> zbase.shrinkZ);
	}

	private boolean canSeeC(int xStart, int yStart, int xEnd, int yEnd) {
		BhStrider b = new BhStrider(xStart, yStart, xEnd, yEnd);
		while (b.hasNext()) {
			if (zbase.isRoad(b.getxNow(), b.getyNow(), zbase.zMax) == false) {
				return false;
			}
			b.next(1);
		}

		return true;
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(zbase.toBytes());
		zb.writeBytesZ(busMap.toBytes());
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		zbase.fromBytes(zb.readBytesZ());
		busMap.fromBytes(zb.readBytesZ());
	}

}
