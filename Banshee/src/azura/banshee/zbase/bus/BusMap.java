package azura.banshee.zbase.bus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import azura.avalon.path.BhStrider;
import azura.banshee.zbase.station.ZbaseR;
import common.algorithm.FastMath;
import common.algorithm.Pack16;
import common.collections.Point;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class BusMap implements BytesI {

	public static Logger log = Logger.getLogger(BusMap.class);

	private static int maxLength = 64;
	private static double distBound = 1;
	// private static double distBound = 1.414;
	// private static double distBound = 2;

	private double averageSpace = 0;
	private double maxSpace = 0;

	public GeoMap gm = new GeoMap();
	private ArrayList<NodeGroup> nodeGroupList = new ArrayList<NodeGroup>();

	public HashMap<Integer, WayDot45> xy_Node = new HashMap<>();

	private ZbaseR zbase;

	public LinkedList<Point> escape(WayDot45 start, Point monster) {
		HashSet<WayDot45> pathSet = new HashSet<>();
		LinkedList<Point> rp = new LinkedList<>();
		rp.add(start.getPoint());
		int distTotal = (int) FastMath.dist(start.x, start.y, monster.x, monster.y);
		pathSet.add(start);
		WayDot45 current = start;
		WayDot45 next = null;
		while (distTotal < 30) {
			next = findNext(monster, current);
			if (pathSet.contains(next))
				break;
			int dist = (int) FastMath.dist(current.x, current.y, next.x, next.y);
			// if ((dist / 2 + distTotal) > 30) {
			// Point mid = new Point();
			// mid.x = (current.x + next.x) / 2;
			// mid.y = (current.y + next.y) / 2;
			// rp.add(mid);
			// break;
			// }
			distTotal += dist;
			pathSet.add(next);
			rp.add(next.getPoint());
		}
		// Queue<Point> rp = new LinkedList<>();
		// rp.forEach(wd -> {
		// rp.add(wd.getPoint());
		// });
		return rp;
	}

	private WayDot45 findNext(Point monster, WayDot45 start) {
		int distMax = 0;
		WayDot45 far = null;
		for (WayDot45 n : start.neighbors) {
			int dist = (int) FastMath.dist(n.x, n.y, monster.x, monster.y);
			if (dist > distMax) {
				distMax = dist;
				far = n;
			}
		}
		if (far == null)
			throw new Error();
		return far;
	}

	public WayDot45 getStation(int xy) {
		// int xy = Pack16.pack(x>>zbase.shrinkZ, y>>zbase.shrinkZ);
		WayDot45 node = xy_Node.get(xy);
		return node;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		gm.width = zb.readZint();
		gm.height = zb.readZint();
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			NodeGroup ng = new NodeGroup();
			ng.fromBytes(zb.readBytesZ());
			nodeGroupList.add(ng);

			for (WayDot45 n : ng.content) {
				xy_Node.put(n.xy, n);
			}
		}

		xy_Node.values().forEach(node -> {
			node.neighborsId.forEach(nId -> {
				WayDot45 n = xy_Node.get(nId);
				node.neighbors.add(n);
			});
			node.neighborsExtId.forEach(nId -> {
				WayDot45 n = xy_Node.get(nId);
				node.neighborsExt.add(n);
			});
		});
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(gm.width);
		zb.writeZint(gm.height);
		zb.writeZint(nodeGroupList.size());
		for (NodeGroup ng : nodeGroupList) {
			zb.writeBytesZ(ng.toBytes());
		}
		return zb.toBytes();
	}

	public void loadFromZbaseR(ZbaseR zbase) {
		this.zbase = zbase;
		maxLength = FastMath.sqrt(zbase.width) * 2;
		gm.width = zbase.width;
		gm.height = zbase.height;

		loadSkeleton();

		ZhangSuenThinning.thin(gm.skeleton, gm.width, gm.height);

		NodeClassifier.classify(gm);

		read();

		findLines();

		expand();

		markFolding();

		checkSpace();

		// markBottleNeck();

		connectNeighbors();

		groupNode();

		mergeNode();

		cordTrans();

		// connectAngle();

		connectNeighborsExt();

	}

	private void loadSkeleton() {
		int w2 = gm.width / 2;
		int h2 = gm.height / 2;
		for (int y = -h2 + 1; y < h2; y++) {
			for (int x = -w2 + 1; x < w2; x++) {
				boolean isRoad = zbase.isRoad(x, y, zbase.zMax);
				if (isRoad) {
					gm.skeletonSet(x + w2, y + h2, true);
				}
			}
		}
	}

	private void read() {

		// skeleton
		for (int s = gm.skeleton.nextSetBit(0); s >= 0; s = gm.skeleton.nextSetBit(s + 1)) {

			int x = s % gm.width;
			int y = s / gm.width;

			WayDot45 node = new WayDot45(x, y, GeoType.Skeleton);
			xy_Node.put(node.xy, node);
		}

		// junction
		Iterator<Integer> ji = gm.junction.iterator();
		while (ji.hasNext()) {
			int t = ji.next();
			int x = t % gm.width;
			int y = t / gm.width;

			WayDot45 node = new WayDot45(x, y, GeoType.Junction);
			xy_Node.put(node.xy, node);
		}

		// terminal
		Iterator<Integer> ti = gm.terminal.iterator();
		while (ti.hasNext()) {
			int t = ti.next();
			int x = t % gm.width;
			int y = t / gm.width;

			WayDot45 node = new WayDot45(x, y, GeoType.Terminal);
			xy_Node.put(node.xy, node);
		}

	}

	private void findLines() {
		HashSet<WayDot45> processedCoreSetIdentity = new HashSet<>();

		for (WayDot45 current : xy_Node.values()) {

			if (current.type == GeoType.Junction || current.type == GeoType.Terminal) {

				// ring 0
				TreeSet<WayDot45> coreSet = new TreeSet<>();
				coreSet.add(current);
				int coreSize = 1;
				do {
					coreSize = coreSet.size();
					f2: for (WayDot45 c : coreSet) {
						for (WayDot45 n0 : getGridNeighbors(c)) {
							if ((n0.type == GeoType.Junction || n0.type == GeoType.Terminal) && !coreSet.contains(n0)) {
								coreSet.add(n0);
								break f2;
							}
						}
					}
				} while (coreSize < coreSet.size());

				WayDot45 identity = coreSet.first();
				if (processedCoreSetIdentity.contains(identity))
					continue;
				else
					processedCoreSetIdentity.add(identity);

				// neighbor junctions
				for (WayDot45 c1 : coreSet) {
					for (WayDot45 c2 : coreSet) {
						if (c1 != c2) {
							Line line = c1.startLine();
							line.end(c2);
						}
					}
				}

				// ring 1
				Set<WayDot45> n1Set = new HashSet<>();
				Set<WayDot45> n2Set = new HashSet<>();
				for (WayDot45 core : coreSet) {
					for (WayDot45 n1c : getGridNeighbors(core)) {
						if (n1c.type == GeoType.Skeleton) {
							n1Set.add(n1c);
						}
					}
				}

				if (n1Set.isEmpty()) {
					for (WayDot45 core : coreSet) {
						core.type = GeoType.Terminal;
					}
					continue;
				}

				// ring 2
				for (WayDot45 n1 : n1Set) {
					for (WayDot45 n2c : getGridNeighbors(n1)) {
						n2Set.add(n2c);
					}
				}
				n2Set.removeAll(n1Set);
				n2Set.removeAll(coreSet);

				// ring 2 back check
				for (WayDot45 n2 : n2Set) {
					WayDot45 head = null;
					WayDot45 neck = null;
					f2: for (WayDot45 n2n : getGridNeighbors(n2)) {
						for (WayDot45 n2nn : getGridNeighbors(n2n)) {
							if (coreSet.contains(n2nn)) {
								head = n2nn;
								neck = n2n;
								break f2;
							}
						}
					}
					if (head != null) {

						Line line = head.startLine();
						line.between.add(neck);

						if (n2.type == GeoType.Terminal || n2.type == GeoType.Junction) {
							line.end(n2);
						} else {
							line.between.add(n2);
						}
					}
				}
			}
		}
	}

	private void expand() {
		for (WayDot45 start : xy_Node.values()) {
			if (start.type == GeoType.Junction || start.type == GeoType.Terminal) {
				ArrayList<Line> cache = new ArrayList<>(start.lines);
				for (Line l : cache) {
					if (l.end != null)
						continue;

					Set<WayDot45> inLine = new HashSet<>();
					inLine.add(start);
					inLine.addAll(l.between);
					WayDot45 current = l.between.get(1);

					while (current != null) {
						Set<WayDot45> nbs = getGridNeighbors(current);
						current = null;
						for (WayDot45 next : nbs) {

							if (inLine.contains(next))
								continue;

							if (next.type == GeoType.Skeleton) {
								l.between.add(next);
								inLine.add(next);
								current = next;
							} else if (next.type == GeoType.Junction || next.type == GeoType.Terminal) {
								l.end(next);
								current = null;
								break;
							}
						}
					}
					if (l.end == null) {
						start.lines.remove(l);
					}
				}
			}
		}
	}

	private void markFolding() {
		for (WayDot45 start : xy_Node.values()) {
			if (start.type == GeoType.Junction || start.type == GeoType.Terminal) {
				ArrayList<Line> cache = new ArrayList<>(start.lines);
				for (Line line : cache) {
					if (!line.removeDup())
						breakLine(line);
				}
			}
		}
	}

	private void breakLine(Line line) {

		if (line.between.size() < 3)
			return;

		WayDot45 start = line.start;
		WayDot45 end = line.end;
		Point startP = new Point(start.x, start.y);
		Point endP = new Point(line.end.x, line.end.y);
		double maxDist = 0;
		WayDot45 folding = null;
		int idx = 0;
		for (int i = 0; i < line.between.size(); i++) {
			WayDot45 n = line.between.get(i);
			Point currentP = new Point(n.x, n.y);
			double dist = FastMath.pointToLineDistance(startP, endP, currentP);
			if (dist > maxDist) {
				maxDist = dist;
				folding = n;
				idx = i;
			}
		}

		boolean doBreak = false;
		if (maxDist >= distBound && idx >= 2 && idx <= line.between.size() - 3) {
			doBreak = true;
		} else if (line.between.size() > maxLength) {
			doBreak = true;
			idx = line.between.size() / 2;
			folding = line.between.get(idx);
		}

		if (doBreak) {

			folding.type = GeoType.Folding;
			Line l2 = folding.startLine();
			l2.between = line.between.subList(idx + 1, line.between.size());
			l2.end(end);

			line.between = line.between.subList(0, idx);
			line.end(folding);

			gm.foldingSet(folding.x, folding.y);

			breakLine(line);
			breakLine(l2);
		}
	}

	private void connectNeighbors() {
		List<WayDot45> all = new ArrayList<>(xy_Node.values());
		for (WayDot45 start : all) {
			if (start.type == GeoType.Junction || start.type == GeoType.Terminal || start.type == GeoType.Folding
					|| start.type == GeoType.BottleNeck) {
				for (Line line : start.lines) {
					line.start.neighbors.add(line.end);
					line.end.neighbors.add(line.start);
				}
				// } else {
				// id_Node.remove(start.id);
			}
		}
	}

	private void groupNode() {
		nodeGroupList.clear();
		for (WayDot45 node : xy_Node.values()) {

			if (node.type == GeoType.Skeleton)
				continue;

			if (node.group != null)
				continue;

			NodeGroup group = new NodeGroup();
			nodeGroupList.add(group);
			node.assignGroup(group);

		}

		// NodeGroup max = nodeGroupList.get(0);
		// for (int i = 1; i < nodeGroupList.size(); i++) {
		// NodeGroup current = nodeGroupList.get(i);
		// if (current.content.size() > max.content.size()) {
		// max = current;
		// }
		// }

	}

	private void mergeNode() {
		for (WayDot45 start : xy_Node.values()) {
			if (start.type == GeoType.Junction) {
				for (Line line : start.lines) {
					if (line.between.size() == 0) {
						mergeCluster(start);
						break;
					}
				}
			}
		}

		removeMerged();
	}

	private void mergeCluster(WayDot45 start) {
		TreeSet<WayDot45> coreSet = new TreeSet<>();
		coreSet.add(start);
		int coreSize = 1;
		do {
			coreSize = coreSet.size();
			f2: for (WayDot45 c : coreSet) {
				for (WayDot45 n0 : getGridNeighbors(c)) {
					if (n0.type == GeoType.Junction && !coreSet.contains(n0)) {
						coreSet.add(n0);
						break f2;
					}
				}
			}
		} while (coreSize < coreSet.size());

		WayDot45 master = coreSet.first();
		coreSet.remove(master);
		for (WayDot45 buddy : coreSet) {
			for (WayDot45 tail : buddy.neighbors) {
				tail.neighbors.remove(buddy);
				tail.neighbors.add(master);
				master.neighbors.add(tail);
			}
			master.neighbors.remove(buddy);
			buddy.type = GeoType.Merged;
			buddy.lines.clear();
			buddy.neighbors.clear();
		}
		master.neighbors.remove(master);
	}

	private void removeMerged() {
		Iterator<Entry<Integer, WayDot45>> it = xy_Node.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, WayDot45> e = it.next();
			WayDot45 node = e.getValue();
			if (node.type == GeoType.Merged) {
				it.remove();
				node.group.content.remove(node);
			}
		}
	}

	private Set<WayDot45> getGridNeighbors(WayDot45 current) {
		Set<WayDot45> nb = new HashSet<>();
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				int xn = current.x + i;
				int yn = current.y + j;

				// int idn = WayDot45.xyToId(xn, yn);
				int idn = Pack16.pack(xn, yn);
				WayDot45 n = xy_Node.get(idn);
				if (n != null)
					nb.add(n);
			}

		return nb;
	}

	private void cordTrans() {
		Iterator<Entry<Integer, WayDot45>> it = xy_Node.entrySet().iterator();
		while (it.hasNext()) {
			WayDot45 node = it.next().getValue();
			// for (WayDot45 node : id_Node.values()) {
			if (node.type == GeoType.Junction || node.type == GeoType.Terminal || node.type == GeoType.Folding
					|| node.type == GeoType.BottleNeck) {
				node.x -= gm.width / 2;
				node.y -= gm.height / 2;
				node.xy = Pack16.pack(node.x, node.y);

				if (node.neighbors.size() == 0) {
					// node = null;
					it.remove();
					log.info("orphan node to null");
				}
			}
		}
	}

	private void connectNeighborsExt() {
		log.info("connect neighbors again");
		int mod = 0;
		Iterator<Entry<Integer, WayDot45>> it = xy_Node.entrySet().iterator();
		while (it.hasNext()) {
			WayDot45 node = it.next().getValue();
			if (node.neighbors.size() < 2)
				continue;
			node.neighborsExt.addAll(node.neighbors);
			// ArrayList<WayDot45> nList = new ArrayList<>(node.neighbors);
			for (WayDot45 one : node.neighbors) {
				for (WayDot45 two : node.neighbors) {
					if (one == two)
						continue;
					if (one.neighborsExt.contains(two))
						continue;

					if (canSee(one.x, one.y, two.x, two.y)) {
						one.neighborsExt.add(two);
						two.neighborsExt.add(one);
						mod++;
					}
				}
			}
		}
		if (mod > 0) {
			log.info("mod " + mod);
			connectNeighborsExt();
		}
	}

	private void checkSpace() {
		int n = 0;
		// int max = 0;
		for (WayDot45 node : xy_Node.values()) {
			if (node.type == GeoType.Junction || node.type == GeoType.Terminal || node.type == GeoType.Folding
					|| node.type == GeoType.BottleNeck) {
				node.space = checkSpaceOne(node.x, node.y);
				averageSpace += node.space;
				n++;
				maxSpace = Math.max(node.space, maxSpace);
				// log.info(dot.space);
			}
		}
		averageSpace /= n;
		log.info("average space = " + averageSpace + " max space = " + maxSpace);
	}

	private int checkSpaceOne(int x, int y) {
		x -= gm.width / 2;
		y -= gm.height / 2;
		SunLight sl = new SunLight(x, y);
		Collection<Point> round = sl.nextRound();
		while (round.isEmpty() == false) {
			Iterator<Point> it = round.iterator();
			while (it.hasNext()) {
				Point p = it.next();
				if (canSee(p.x, p.y, x, y) == false) {
					int dist = (int) FastMath.dist(x, y, p.x, p.y);
					return dist;
				}
			}
			round = sl.nextRound();
		}
		throw new Error();
	}

	private boolean canSee(int xStart, int yStart, int xEnd, int yEnd) {
		BhStrider b = new BhStrider(xStart, yStart, xEnd, yEnd);
		while (b.hasNext()) {
			if (zbase.isRoad(b.getxNow(), b.getyNow(), zbase.zMax) == false) {
				return false;
			}
			b.next(1);
		}
		return true;
	}

	private Point rayCast(int xStart, int yStart, int xEnd, int yEnd) {
		BhStrider b = new BhStrider(xStart, yStart, xEnd, yEnd);
		while (b.hasNext()) {
			if (zbase.isRoad(b.getxNow() - gm.width / 2, b.getyNow() - gm.height / 2, zbase.zMax) == false) {
				return new Point(b.getxNow(), b.getyNow());
			}
			b.next(1);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void markBottleNeck() {
		for (WayDot45 start : xy_Node.values()) {
			if (start.type == GeoType.Junction || start.type == GeoType.Terminal || start.type == GeoType.Folding) {
				ArrayList<Line> cache = new ArrayList<>(start.lines);
				for (Line line : cache) {
					if (line.between.size() <= 5)
						continue;

					int idx = line.between.size() / 2;
					WayDot45 mid = line.between.get(idx);

					Seg s = new Seg(line.start.x, line.start.y, line.end.x, line.end.y, maxSpace * 2);

					WingSpanResult spanMid = getWingSpan(mid, s);
					if (spanMid.balanced == false)
						continue;

					double limit = maxSpace * 2;
					if (spanMid.span > limit)
						continue;

					WingSpanResult span1 = getWingSpan(line.start, s);
					WingSpanResult span2 = getWingSpan(line.end, s);

					// boolean bottleShape = spanMid.span < (span1.span + 1) &&
					// spanMid.span < (span2.span + 1);
					boolean bottleShape = spanMid.span * 2 < (span1.span + span2.span + 2);
					if (bottleShape == false)
						continue;

					mid.space = (int) (spanMid.span / 2);
					mid.type = GeoType.BottleNeck;

					// line.start.neighbors.add(mid);
					// line.end.neighbors.add(mid);
					// mid.neighbors.add(line.start);
					// mid.neighbors.add(line.end);

					Line l2 = mid.startLine();
					l2.between = line.between.subList(idx + 1, line.between.size());
					l2.end(line.end);

					line.between = line.between.subList(0, idx);
					line.end(mid);

					gm.bottleNeckSet(mid.x, mid.y);
				}
			}
		}
	}

	// =======================================

	private WingSpanResult getWingSpan(WayDot45 from, Seg s) {
		WingSpanResult wsr = new WingSpanResult();
		Point p1 = s.getT1(from);
		Point p2 = s.getT2(from);
		Point hit1 = rayCast(from.x, from.y, p1.x, p1.y);
		if (hit1 == null) {
			hit1 = p1;
			wsr.balanced = false;
		}
		Point hit2 = rayCast(from.x, from.y, p2.x, p2.y);
		if (hit2 == null) {
			hit2 = p2;
			wsr.balanced = false;
		}
		wsr.dist1 = FastMath.dist(from.x, from.y, hit1.x, hit1.y);
		wsr.dist2 = FastMath.dist(from.x, from.y, hit2.x, hit2.y);
		wsr.balanced = FastMath.similar(wsr.dist1, wsr.dist2);
		wsr.span = wsr.dist1 + wsr.dist2;
		return wsr;
	}

	@SuppressWarnings("unused")
	private boolean isNeighbor(WayDot45 start, WayDot45 end) {
		int d1 = Math.abs(start.x - end.x);
		int d2 = Math.abs(start.y - end.y);
		return d1 < 2 && d2 < 2;
	}

	@SuppressWarnings("unused")
	private void killOrphan() {
		ArrayList<NodeGroup> groups = new ArrayList<>();
		for (WayDot45 node : xy_Node.values()) {
			if (node.group != null)
				continue;

			NodeGroup group = new NodeGroup();
			groups.add(group);
			node.assignGroup(group);
		}

		NodeGroup max = groups.get(0);
		for (int i = 1; i < groups.size(); i++) {
			NodeGroup current = groups.get(i);
			if (current.content.size() > max.content.size()) {
				max = current;
			}
		}

		for (NodeGroup ng : groups) {
			if (ng == max)
				continue;

			for (WayDot45 o : ng.content) {
				o.type = GeoType.Orphan;
				o.neighbors.clear();
				o.lines.clear();
			}
		}
	}

	@SuppressWarnings("unused")
	private void drawLineG(BufferedImage bi) {
		Graphics2D g = bi.createGraphics();
		NodeGroup group = null;
		for (WayDot45 start : xy_Node.values()) {
			for (WayDot45 end : start.neighbors) {
				if (start.xy > end.xy) {
					if (start.group != group) {
						group = start.group;
						g.setColor(new Color(group.color, false));
					}
					g.drawLine(start.x + gm.width / 2, start.y + gm.height / 2, end.x + gm.width / 2,
							end.y + gm.height / 2);
				}
			}
		}
		g.dispose();
	}

	@SuppressWarnings("unused")
	private void drawLineB(BufferedImage bi) {
		NodeGroup group = null;
		for (WayDot45 start : xy_Node.values()) {
			for (WayDot45 end : start.neighbors) {
				if (start.xy > end.xy) {
					if (start.group != group) {
						group = start.group;
					}
					BhStrider bh = new BhStrider(start.x + gm.width / 2, start.y + gm.height / 2, end.x + gm.width / 2,
							end.y + gm.height / 2);
					while (bh.hasNext()) {
						bi.setRGB(bh.getxNow(), bh.getyNow(), group.color);
						bh.next(1);
					}
				}
			}
		}
	}

	public BufferedImage draw(BufferedImage bi) {

		drawLineB(bi);

		int junction = 0;
		int terminal = 0;
		int folding = 0;
		int bottleNeck = 0;
		// g.setColor(Color.BLUE);
		for (WayDot45 node : xy_Node.values()) {
			if (node.type == GeoType.Junction) {
				bi.setRGB(node.x + gm.width / 2, node.y + gm.height / 2, 0xffff0000);
				junction++;
				// System.out.println("junction:
				// neighbor="+node.neighbors.size()+" id="+node.id);
			} else if (node.type == GeoType.Terminal) {
				bi.setRGB(node.x + gm.width / 2, node.y + gm.height / 2, 0xff0000ff);
				terminal++;
				// System.out.println("terminal:
				// neighbor="+node.neighbors.size()+" id="+node.id);
			} else if (node.type == GeoType.Folding) {
				bi.setRGB(node.x + gm.width / 2, node.y + gm.height / 2, 0xffff8800);
				folding++;
				// System.out.println("folding:
				// neighbor="+node.neighbors.size()+" id="+node.id);
			} else if (node.type == GeoType.BottleNeck) {
				bi.setRGB(node.x + gm.width / 2, node.y + gm.height / 2, 0xff000000);
				bottleNeck++;
			}
		}
		// g.dispose();

		System.out.println("junction=" + junction + " terminal=" + terminal + " folding=" + folding + " bottleNeck="
				+ bottleNeck + " all=" + (junction + terminal + folding + bottleNeck));

		return bi;
	}

}

// @SuppressWarnings("unused")
// private void connectAngle(Line one, Line two) {
// if (one == null || two == null)
// return;
//
// if (one.start == two.start) {
// boolean success = tryConnect(one.end, two.end);
// if (success) {
// connectAngle(forward(one, two.end), forward(two, one.end));
// }
// } else {
// int angle1 = FastMath.xoy2Angle(one.end.getPoint(), one.start.getPoint(),
// two.start.getPoint());
// int angle2 = FastMath.xoy2Angle(two.end.getPoint(), two.start.getPoint(),
// one.start.getPoint());
// if (angle1 > 160 && angle2 > 160) {
// return;
// } else if (angle1 < angle2) {
// boolean success = tryConnect(one.end, two.start);
// if (success) {
// connectAngle(forward(one, two.start), two);
// }
// } else {
// boolean success = tryConnect(one.start, two.end);
// if (success) {
// connectAngle(one, forward(two, two.end));
// }
// }
// }
// }
//
// private Line forward(Line old, WayDot45 skip) {
// if (old.end.type == GeoType.Junction)
// return null;
// for (WayDot45 n : old.end.neighbors) {
// if (n == old.start || n == skip)
// continue;
// Line next = new Line(old.end);
// next.end = n;
// return next;
// }
// return null;
// }
//
// private boolean tryConnect(WayDot45 one, WayDot45 two) {
// // if (canSee(one.x - gm.width / 2, one.y - gm.height / 2, two.x -
// // gm.width / 2, two.y - gm.height / 2)) {
// // if(one==two)
// // return false;
// // if (one.neighborPlus.contains(two))
// // return false;
// if (canSee(one.x, one.y, two.x, two.y)) {
//// one.neighborPlus.add(two);
//// two.neighborPlus.add(one);
// return true;
// } else
// return false;
// }

// private void connectAngle() {
// Iterator<Entry<Integer, WayDot45>> it = xy_Node.entrySet().iterator();
// while (it.hasNext()) {
// WayDot45 node = it.next().getValue();
// if (node.type == GeoType.Junction) {
// if (node.neighbors.size() != 3) {
// log.info("strange junction: " + node.neighbors.size());
// continue;
// }
//
// ArrayList<Line> lineList = new ArrayList<>();
// for (WayDot45 to : node.neighbors) {
// Line line = new Line(node);
// line.end = to;
// lineList.add(line);
// }
//
// connectAngle(lineList.get(0), lineList.get(1));
// connectAngle(lineList.get(0), lineList.get(2));
// connectAngle(lineList.get(1), lineList.get(2));
// }
// }
//
// it = xy_Node.entrySet().iterator();
// while (it.hasNext()) {
// WayDot45 node = it.next().getValue();
// node.neighbors.addAll(node.neighborPlus);
// }
// }

// public void loadFromImage(ImageReader2 reader) {
// gm.width = reader.frameWidth(reader.zMax);
// gm.height = reader.frameHeight(reader.zMax);
// int w2 = gm.width / 2;
// int h2 = gm.height / 2;
// for (int y = reader.bbc.getCeiling(); y <= reader.bbc.getFloor(); y++) {
// for (int x = reader.bbc.getLeft(); x <= reader.bbc.getRight(); x++) {
// int color = reader.getRGB(x, y, reader.zMax);
// boolean isRoad = (color == 0xffffffff);
// if (isRoad) {
// gm.skeletonSet(x + w2, y + h2, true);
// }
// }
// }
// ZhangSuenThinning.thin(gm.skeleton, gm.width, gm.height);
// NodeClassifier.classify(gm);
// // load(gm);
//
// read();
//
// findLines();
//
// expand();
//
// markTurn();
//
// connectNeighbors();
//
// groupNode();
//
// mergeNode();
//
// cordTrans();
// }