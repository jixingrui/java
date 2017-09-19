package old;

import java.awt.Point;

public class HexagonOld {

	private static double sqrt2 = Math.sqrt(2);
	private static double sqrt3 = Math.sqrt(3);

	public static boolean inHex(double x, double y, double w, double h) {
		if (Math.abs(y - (h - 1) / 2) >= w * sqrt3 / 4) {
			return false;
		} else if (w / 2 - Math.abs(x - (w - 1) / 2) > Math.abs(y - (h - 1) / 2) / sqrt3) {
			return true;
		} else {
			return false;
		}
	}

	public static Point top2flat(double x, double y, double w, double h, int n) {

		if (!inHex(x, y, w, h)) {
			return null;
		}

		double xd = x - (w - 1) / 2;
		double yd = y - (h - 1) / 2;

		double atan = 0;
		if (xd == 0 && yd == 0) {
			return new Point(0, 0);
		} else if (xd == 0 && yd > 0) {
			atan = Math.PI / 2;
		} else if (xd == 0 && yd < 0) {
			atan = Math.PI / 2 * 3;
		} else {
			atan = Math.atan2(yd, xd);
		}

		double upn = Math.sqrt(xd * xd + yd * yd) * Math.cos(atan - n * Math.PI / 3);
		double vpn = Math.sqrt(xd * xd + yd * yd) * Math.sin(atan - n * Math.PI / 3) / sqrt2;

		return new Point((int) Math.round(upn + (w - 1) / 2), (int) Math.round(vpn + (h - 1) / 2));
	}

	public static Point flat2top(double u, double v, double w, double h, int n) {

		double ud = u - (w - 1) / 2;
		double vd = v - (h - 1) / 2;

		double atan = 0;
		if (ud == 0 && vd == 0) {
			return new Point(0, 0);
		} else if (ud == 0 && vd > 0) {
			atan = Math.PI / 2;
		} else if (ud == 0 && vd < 0) {
			atan = Math.PI / 2 * 3;
		} else {
			atan = Math.atan2(sqrt2 * vd, ud);
		}

		double xd = Math.sqrt(ud * ud + 2 * vd * vd) * Math.cos(atan + n * Math.PI / 3);
		double yd = Math.sqrt(ud * ud + 2 * vd * vd) * Math.sin(atan + n * Math.PI / 3);

		int x = (int) Math.round(xd + (w - 1) / 2);
		int y = (int) Math.round(yd + (h - 1) / 2);

		if (inHex(x, y, w, h)) {
			return new Point(x, y);
		} else {
			return null;
		}

	}

}
