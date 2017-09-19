package azura.banshee.zbase.bus;

import java.util.Collection;
import java.util.TreeSet;

import common.algorithm.FastMath;
import common.collections.Point;

public class SunLight {
	private Point start;
	private int round;
	private TreeSet<Point> outerLayer = new TreeSet<>();

	public SunLight(int x, int y) {
		start = new Point(x, y);
	}

	private void init1() {
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				Point current = new Point(start.x + i, start.y + j);
				outerLayer.add(current);
			}
	}

	private void init2() {
		outerLayer.clear();
		for (int i = -2; i <= 2; i++)
			for (int j = -2; j <= 2; j++) {
				if (Math.abs(i) == 2 || Math.abs(j) == 2) {
					Point current = new Point(start.x + i, start.y + j);
					outerLayer.add(current);
				}
			}
	}

	public Collection<Point> nextRound() {
		if (round == 0) {
			init1();
		} else if (round == 1) {
			init2();
		} else {
			cast();
		}
		round++;
		return outerLayer;
	}

	private void cast() {
		TreeSet<Point> lastLayer = outerLayer;
		outerLayer = new TreeSet<>();
		for (Point p : lastLayer) {
			castOne(p);
		}
	}

	private void castOne(Point m) {
		int smx = m.x - start.x;
		int smy = m.y - start.y;
		int big = Math.max(Math.abs(smx), Math.abs(smy));
		int small = Math.min(Math.abs(smx), Math.abs(smy));
		double p = big + 1;
		double q = p * small / big;
		int d = FastMath.sign(Math.abs(smx) - Math.abs(smy));

		if (FastMath.isInteger(q)) {
			int skx = Rex(p, q, d, smx);
			int sky = Rey(p, q, d, smy);
			Point k = new Point(skx + start.x, sky + start.y);
			outerLayer.add(k);
		} else {
			int Ap = (int) p;
			int Bp = (int) p;
			int Aq = (int) Math.floor(q);
			int Bq = (int) Math.ceil(q);

			// A
			int sAx = Rex(Ap, Aq, d, smx);
			int sAy = Rey(Ap, Aq, d, smy);
			Point A = new Point(sAx + start.x, sAy + start.y);
			outerLayer.add(A);

			// B
			int sBx = Rex(Bp, Bq, d, smx);
			int sBy = Rey(Bp, Bq, d, smy);
			Point B = new Point(sBx + start.x, sBy + start.y);
			outerLayer.add(B);
		}
	}

	private int Rex(double w, double v, int d, int smx) {
		return (int) ((0.5 * (1 + d) * w + 0.5 * (1 - d) * v) * FastMath.sign(smx));
	}

	private int Rey(double w, double v, int d, int smy) {
		return (int) ((0.5 * (1 - d) * w + 0.5 * (1 + d) * v) * FastMath.sign(smy));
	}
}
