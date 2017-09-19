package azura.banshee.zbase.bus;

import common.algorithm.FastMath;
import common.collections.Point;

public class Seg {

	private int dx;
	private int dy;

	public Seg(int xa, int ya, int xb, int yb, double lth) {
		double dab = FastMath.dist(ya, xa, yb, xb);
		dx = (int) (lth * (ya - yb) / dab);
		dy = (int) (lth * (xa - xb) / dab);
	}

	public Point getT1(WayDot45 from) {
		Point result = new Point();
		result.x = -dx + from.x;
		result.y = dy + from.y;
		return result;
	}

	public Point getT2(WayDot45 from) {
		Point result = new Point();
		result.x = dx + from.x;
		result.y = -dy + from.y;
		return result;
	}

}
