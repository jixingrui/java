package common.algorithm;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import common.logger.Trace;

public class FoldIndexOld3 {

	/**
	 * @param x
	 *            {0} {0,1} {0,1,2,3} ...
	 * @param y
	 *            {0} {0,1} {0,1,2,3} ...
	 * @param z
	 *            [0,15]
	 * @return fi
	 */
	public static int mix(int x, int y, int z) {
		if (z < 0 || z > 15)
			return -1;
		int bound = getBound(z);
		if (x < 0 || x >= bound || y < 0 || y >= bound)
			return -1;
		int result = 1 << (z * 2);
		result |= x << z;
		result |= y;
		return result - 1;
	}

	/**
	 * @return int[3]={x,y,z}
	 */
	public static int[] getXyz(int fi) {
		if (++fi <= 0)
			return new int[] { -1, -1, -1 };

		int[] xyz = new int[3];
		int z = FastMath.log2(fi) >>> 1;
		int mask = FastMath.pow2(z) - 1;
		int x = (fi >>> z) & mask;
		int y = fi & mask;
		xyz[0] = x;
		xyz[1] = y;
		xyz[2] = z;
		return xyz;
	}

	/**
	 * @param z
	 *            [0,30]
	 * @return exclusive
	 */
	public static int getBound(int z) {
		return FastMath.pow2(z);
	}

	public static int getZ(int fi) {
		fi++;
		return FastMath.log2(fi) >>> 1;
	}

	public static int getUp(int fi) {
		if (fi <= 0) {
			return -1;
		} else {
			int[] xyz = getXyz(fi);
			return mix(xyz[0] >>> 1, xyz[1] >>> 1, xyz[2] - 1);
		}
	}

	public static int[] getLow4(int fi) {
		int[] xyz = getXyz(fi);
		if (xyz[2] < 0 || xyz[2] >= 15)
			return new int[] { -1, -1, -1, -1 };

		int[] result = new int[4];
		result[0] = mix(xyz[0] << 1, xyz[1] << 1, xyz[2] + 1);
		result[1] = mix(xyz[0] << 1 | 1, xyz[1] << 1, xyz[2] + 1);
		result[2] = mix(xyz[0] << 1, xyz[1] << 1 | 1, xyz[2] + 1);
		result[3] = mix(xyz[0] << 1 | 1, xyz[1] << 1 | 1, xyz[2] + 1);
		return result;
	}

	public static int getChamberCount(int zMax) {
		if (zMax < 0 || zMax > 15)
			return -1;
		else
			return -1 >>> (31 - zMax * 2);
	}

	public static List<Integer> getFiOnLayer(int z) {
		int bound = getBound(z);
		List<Integer> fiOnLayer = new ArrayList<Integer>(bound * bound);
		for (int i = 0; i < bound; i++)
			for (int j = 0; j < bound; j++) {
				int fi = mix(i, j, z);
				fiOnLayer.add(fi);
			}
		return fiOnLayer;
	}

	private static void show(Integer fi) {
		int[] exp = getXyz(fi);
		System.out.print("x=" + exp[0] + ",y=" + exp[1] + ",z=" + exp[2]
				+ "\tfi=" + fi + "\n");
	}

	/**
	 * @param zTc
	 *            z to chamber
	 * @param zOc
	 *            z of chamber
	 * @return
	 */
	public static List<Integer> getOverlapChambers(Rectangle viewRect, int zTc,
			int zOc) {

		int bound = getBound(zTc + zOc);

		int left = FastMath.bound(viewRect.x, 0, bound) >> zTc;
		int right = FastMath.bound(viewRect.x + viewRect.width - 1, 0, bound) >> zTc;
		int up = FastMath.bound(viewRect.y, 0, bound) >> zTc;
		int down = FastMath.bound(viewRect.y + viewRect.height - 1, 0, bound) >> zTc;

		List<Integer> result = new ArrayList<Integer>();

		for (int x = left; x <= right; x++)
			for (int y = up; y <= down; y++) {
				int fi = FoldIndexOld.mix(x, y, zOc);
				if (fi >= 0)
					result.add(fi);
			}
		return result;
	}

	public static List<Integer> covers(int left, int right, int top,
			int bottom, int z) {

		int bound = getBound(z);

		left = FastMath.bound(left, 0, bound);
		right = FastMath.bound(right, 0, bound);
		top = FastMath.bound(top, 0, bound);
		bottom = FastMath.bound(bottom, 0, bound);

		List<Integer> result = new ArrayList<Integer>();

		for (int x = left; x <= right; x++)
			for (int y = top; y <= bottom; y++) {
				int fi = FoldIndexOld.mix(x, y, z);
				if (fi >= 0)
					result.add(fi);
			}
		return result;
	}

	@Test
	public static void testChamber() {
		Rectangle r = new Rectangle(-83, -93, 1934, 314);
		// r = restrict(r, 5);
		List<Integer> list = getOverlapChambers(r, 2, 3);

		Trace.trace(list.size());

		// r.x = FastMath.random(-10, 100);
		// r.y = FastMath.random(-10, 100);
		// r.width = FastMath.random(-10, 100);
		// r.height = FastMath.random(-10, 100);
		//
		// r.height=10000;
		//
		// r = restrict(r, 7);

		// Trace.trace(r);

		// for (int z = 0; z <= 15; z++) {
		// int bound = getBound(z);
		// int fi = mix(bound - 1, bound - 1, z);
		// Trace.trace(z + ":" + getChamberCount(z) + " fiMax=" + fi
		// + " bound=" + bound);
		// }
	}

	/**
	 * @param zTc
	 *            z to chamber
	 */
	public static boolean crossedChamber(Rectangle r1, Rectangle r2, int zTc) {
		return (r1.x >> zTc != r2.x >> zTc)
				|| (r1.y >> zTc != r2.y >> zTc)
				|| ((r1.x + r1.width - 1) >> zTc != (r2.x + r2.width - 1) >> zTc)
				|| ((r1.y + r1.height - 1) >> zTc != (r2.y + r2.height - 1) >> zTc);
	}

	// @Test
	public static void testManual() {
		for (int z = 0; z < 5; z++)
			for (int x = 0; x < getBound(z); x++)
				for (int y = 0; y < getBound(z); y++) {
					int fi = mix(x, y, z);
					show(fi);
				}
	}

	// @Test(invocationCount = 10000)
	public static void testBasic() {
		int z = FastMath.random(0, 16);
		int bound = getBound(z);
		int x = FastMath.random(0, bound);
		int y = FastMath.random(0, bound);

		int fi = mix(x, y, z);

		int[] xyz = getXyz(fi);

		Assert.assertEquals(xyz, new int[] { x, y, z });
	}

	// @Test(invocationCount = 1000)
	public static void testUpLow() {

		int fi = FastMath.random(0, Integer.MAX_VALUE);
		int[] xyz = getXyz(fi);
		fi = mix(xyz[0], xyz[1], xyz[2]);

		if (xyz[2] < 15) {
			int[] fiLow = getLow4(fi);
			for (int i = 0; i < 4; i++) {
				int fiBack = getUp(fiLow[i]);
				Assert.assertEquals(fi, fiBack);
			}
		}
	}

	public static void println(int fi) {
		int[] xyz = getXyz(fi);
		System.out.println("fi= " + fi + " x=" + xyz[0] + " y=" + xyz[1]
				+ " z=" + xyz[2]);
	}

}
