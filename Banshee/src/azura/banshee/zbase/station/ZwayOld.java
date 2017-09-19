package azura.banshee.zbase.station;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import azura.avalon.path.BhStrider;
import azura.banshee.zbase.Zbase;
import azura.banshee.zbase.bus.BusMap;
import azura.banshee.zbase.bus.GeoType;
import azura.banshee.zbase.bus.SunLight;
import azura.banshee.zbase.bus.WayDot45;
import common.algorithm.FastMath;
import common.collections.Point;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.graphics.ImageUtil;

public class ZwayOld implements BytesI {
	public static Logger log = Logger.getLogger(ZwayOld.class);

	Zbase zbase = new Zbase();
	BusMap busMap = new BusMap();

	public void load(Zbase zbase) throws IOException {
		this.zbase = zbase;
		// busMap.loadFromZbase(zbase);
	}

	// ======================= io ====================
	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		zbase.fromBytes(zb.readBytesZ());
		busMap.fromBytes(zb.readBytesZ());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(zbase.toBytes());
		zb.writeBytesZ(busMap.toBytes());
		return zb.toBytes();
	}

	// ==================== draw =====================
	public void draw(String output) throws IOException {

		BufferedImage way = ImageUtil.newImage(zbase.width, zbase.height, new Color(0xff000000, true));

		zbase.draw(way, new Color(0x0, true), 0, 0, 0);
		busMap.draw(way);

		// ======================= debug =========================
		ImageIO.write(way, "png", new File(output));
	}

	// private BitSet bsContour = new BitSet();
	private TreeSet<Point> contour = new TreeSet<>();

	public void drawSkeleton(String output) throws IOException {

		BufferedImage way = ImageUtil.newImage(zbase.width, zbase.height, new Color(0x0, false));

		zbase.draw(way, Color.WHITE, 0, 0, 0);

		busMap.gm.draw(way);
		ImageIO.write(way, "png", new File(output));
		return;
	}

	public void drawGravity(String output) throws IOException {

		BufferedImage way = ImageUtil.newImage(zbase.width, zbase.height, new Color(0x0, false));

		zbase.draw(way, Color.WHITE, 0, 0, 0);

		// int want = FastMath.random(1, busMap.id_Node.size());
		// int count = 0;
		for (WayDot45 dot : busMap.xy_Node.values()) {
			if (dot.type == GeoType.Skeleton)
				continue;
			// count++;
			// if (count < want)
			// continue;
			// else if (count > want)
			// break;

			// int hitDist = Integer.MAX_VALUE;
			SunLight rc = new SunLight(dot.x, dot.y);
			Collection<Point> round = rc.nextRound();
			while (round.isEmpty() == false) {
				drawRound(round, dot.x, dot.y, way, 300);
				// hitDist = Math.min(hit, hitDist);
				round = rc.nextRound();
			}
			// log.info("(" + dot.x + "," + dot.y + ") emptiness=" + hitDist);
		}

		smoothContour();

		for (Point c : contour) {
			way.setRGB(c.x + zbase.width / 2, c.y + zbase.height / 2, 0xffff0000);
		}

		busMap.draw(way);

		// ======================= debug =========================
		ImageIO.write(way, "png", new File(output));
	}

	private void smoothContour() {
		LinkedList<Point> rawQue = new LinkedList<>(contour);
		contour = new TreeSet<>();
		while (rawQue.isEmpty() == false) {
			Point raw = rawQue.removeFirst();
			if (isContour(raw))
				contour.add(raw);
		}

		LinkedList<Point> freshMeat = new LinkedList<>(contour);
		while (freshMeat.isEmpty() == false) {
			Point check = freshMeat.removeFirst();
			for (Point n : n9(check)) {
				if (contour.contains(n))
					continue;
				if (isContour(n)) {
					contour.add(n);
					freshMeat.addFirst(n);
				}
			}
		}
	}

	private boolean isContour(Point p) {
		if (zbase.isRoad(p.x, p.y, zbase.zMax) == false)
			return false;
		if (zbase.isRoad(p.x + 1, p.y, zbase.zMax) == false)
			return true;
		if (zbase.isRoad(p.x - 1, p.y, zbase.zMax) == false)
			return true;
		if (zbase.isRoad(p.x, p.y + 1, zbase.zMax) == false)
			return true;
		if (zbase.isRoad(p.x, p.y - 1, zbase.zMax) == false)
			return true;
		return false;
	}

	private List<Point> n9(Point check) {
		ArrayList<Point> result = new ArrayList<>();
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				Point n = new Point(check.x + i, check.y + j);
				result.add(n);
			}
		return result;
	}

	private int drawRound(Collection<Point> round, int x, int y, BufferedImage way, int limit) {
		int hitDist = Integer.MAX_VALUE;
		Iterator<Point> it = round.iterator();
		while (it.hasNext()) {
			Point p = it.next();
			int dist = (int) FastMath.dist(p.x, p.y, x, y);
			if (dist > limit) {
				it.remove();
			} else if (canSeeC(p.x, p.y, x, y)) {
				int blue = dist + 100;
				int newC = 0xff000000 | blue;
				int oldC = way.getRGB(p.x + zbase.width / 2, p.y + zbase.height / 2);
				if (newC < oldC) {
					way.setRGB(p.x + zbase.width / 2, p.y + zbase.height / 2, newC);
				}
			} else {
				it.remove();
				hitDist = Math.min(dist, hitDist);
			}
		}
		return hitDist;
	}

	private boolean canSeeC(int xStart, int yStart, int xEnd, int yEnd) {
		BhStrider b = new BhStrider(xStart, yStart, xEnd, yEnd);
		int lastX = xEnd;
		int lastY = yEnd;
		while (b.hasNext()) {
			if (zbase.isRoad(b.getxNow(), b.getyNow(), zbase.zMax) == false) {
				contour.add(new Point(lastX, lastY));
				return false;
			}
			lastX = b.getxNow();
			lastY = b.getyNow();
			b.next(1);
		}

		return true;
	}

	public void drawGravityOld(String output) throws IOException {

		BufferedImage way = ImageUtil.newImage(zbase.width, zbase.height, new Color(0xff000000, true));

		zbase.draw(way, new Color(0x0, true), 0, 0, 0);

		int r = 64;
		for (WayDot45 dot : busMap.xy_Node.values()) {
			for (int i = dot.x - r; i < dot.x + r; i++)
				for (int j = dot.y - r; j < dot.y + r; j++) {
					int dist = (int) FastMath.dist(i, j, dot.x, dot.y);
					// int distS = FastMath.sqrt(dist);
					if (dist < 64 && zbase.isRoad(i, j, zbase.zMax)) {
						// int newC = 0xff000000 | (dist & 0xff);
						int blue = Math.min(0xff, dist * 4);
						int newC = 0xff000000 | blue;
						int oldC = way.getRGB(i + zbase.width / 2, j + zbase.height / 2);
						if (newC < oldC)
							way.setRGB(i + zbase.width / 2, j + zbase.height / 2, newC);
					}
				}
		}

		// ======================= debug =========================
		ImageIO.write(way, "png", new File(output));
	}

}

// private static void draw(Zbase zbase, int z) throws IOException {
// int half = FoldIndex.sideLength(z) * 256 / 2;
// BufferedImage bi = ImageUtil.newImage(half * 2, half * 2, Color.BLACK);
//
// for (int i = -half; i < half; i++)
// for (int j = -half; j < half; j++) {
// if (i == 4 && j == 4 && z == 1)
// log.info("stop");
// if (zbase.isRoad(i, j, z)) {
// bi.setRGB(i + half, j + half, 0xffffff);
// }
// }
//
// // ImageUtil.writePng(bi, "z:/map/��װ��v8/shop.base.debug" + z + ".png");
// Debug.draw(bi, "z:/map/debug/" + z + ".png");
// }

// public static void main(String[] args) throws IOException {
// String source = "Z:/map/��װ��v8/shop.base.tif";
//
// Zway zway = new Zway();
// zway.load(source);
//
// for (int z = zway.zbase.zMax; z >= 0; z--) {
// draw(zway.zbase, z);
// }
//
// }
