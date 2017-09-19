package azura.banshee.main;

import common.algorithm.FastMath;
import common.logger.Trace;

public class Neck {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// double[] top = new double[](-11, -17);
		// int rotation = 160;
		// double[] flat = topToFlat(top, rotation);

		for (int i = 0; i < 100; i++) {
			int x = FastMath.random(-300, 300);
			int y = FastMath.random(-300, 300);
			int angle = FastMath.random(0, 360);

			double[] flat = new double[] { x, y };
			double[] top = flatToTop(flat, angle);
			double[] flatBack = topToFlat(top, angle);

			int xb = (int) Math.round(flatBack[0]);
			int yb = (int) Math.round(flatBack[1]);
			if (x != xb || y != yb)
				Trace.trace("flat= (" + flat[0] + "," + flat[1] + ") top= ("
						+ top[0] + "," + top[1] + ") flatBack= (" + flatBack[0]
						+ "," + flatBack[1] + ")" + " (" + xb + "," + yb + ")");
		}
	}

	/**
	 * @param top
	 *            center [x,y]
	 * @param angle
	 *            [0,360)
	 * @return flat center [x,y]
	 */
	public static double[] topToFlat(double[] top, int angle) {
		double[] flat = new double[2];
		double arc = (double) Math.toRadians(angle);
		double atan = Math.atan2(top[1], top[0]);
		flat[0] = Math.sqrt(Math.pow(top[0], 2) + Math.pow(top[1], 2))
				* Math.cos(atan - arc);
		flat[1] = Math.sqrt((Math.pow(top[0], 2) + Math.pow(top[1], 2)) / 2)
				* Math.sin(atan - arc);
		return flat;
	}

	/**
	 * @param flat
	 *            center [x,y]
	 * @param angle
	 *            [0,360)
	 * @return top center [x,y]
	 */
	public static double[] flatToTop(double[] flat, int angle) {
		double[] top = new double[2];
		double arc = Math.toRadians(angle);
		double atan = Math.atan2(flat[1] * Math.sqrt(2), flat[0]);
		top[0] = Math.sqrt(Math.pow(flat[0], 2) + 2 * Math.pow(flat[1], 2))
				* Math.cos(atan + arc);
		top[1] = Math.sqrt(Math.pow(flat[0], 2) + 2 * Math.pow(flat[1], 2))
				* Math.sin(atan + arc);
		return top;
	}
}
