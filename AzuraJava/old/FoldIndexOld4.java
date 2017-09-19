package common.algorithm;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.logger.Trace;

public class FoldIndexOld4 {

	public static void main(String[] args) throws IOException {
		int fi = mix(0, 0, 0);
		// Trace.trace(fi + "=" + Integer.toBinaryString(fi));
		show(fi);
		int[] xyz = getXyz(fi);
		Trace.trace(xyz[0] + "," + xyz[1] + "," + xyz[2]);

		// Trace.trace(Integer.MAX_VALUE);

		// IntMap<Integer> fi_Chamber = new IntMap<Integer>();
		// HashMap<Integer, Integer> hm = new HashMap<>();
		//
		// long start = System.currentTimeMillis();
		// for (int i = 0; i < 1000000; i++) {
		// fi = mix(FastMath.random(0, 10000), FastMath.random(0, 10000), 15);
		// fi_Chamber.put(fi, i);
		// // hm.put(fi, i);
		// }
		//
		// Entries<Integer> it = fi_Chamber.entries();
		// while (it.hasNext) {
		// it.next();
		// it.remove();
		// }
		// fi_Chamber.shrink(fi_Chamber.size * 2);
		// // for(int k:hm.keySet())
		// // fi_Chamber.remove(k);
		// // fi_Chamber.clear();
		// // fi_Chamber.shrink(32);
		// long end = System.currentTimeMillis();
		// Trace.trace(end - start);
		// System.in.read();
		// Trace.trace(fi_Chamber.size);
		// Trace.trace(hm.size());
	}

	/**
	 * @param x
	 *            {0} {0,1} {0,1,2,3} ... {0...32767}
	 * @param y
	 *            {0} {0,1} {0,1,2,3} ... {0...32767}
	 * @param z
	 *            [0,1,2 ... 15]
	 * @return fi [1,2147483647]
	 */
	public static int mix(int x, int y, int z) {
		int result = 1 << (z * 2);
		result |= x << z;
		result |= y;
		return result;
	}

	public static boolean isValid(int x, int y, int z) {
		if (z < 0 || x < 0 || y < 0 || z > 15)
			return false;
		int bound = getBound(z);
		if (x >= bound || y >= bound)
			return false;
		return true;
	}

	/**
	 * @return int[3]={x,y,z}
	 */
	public static int[] getXyz(int fi) {
		if (fi <= 0)
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
	 *            [0,15]
	 * @return
	 */
	public static int getBound(int z) {
		return FastMath.pow2(z);
	}

	public static int getZ(int fi) {
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
		if (xyz[2] <= 0 || xyz[2] >= 15)
			return new int[] { -1, -1, -1, -1 };

		int[] result = new int[4];
		result[0] = mix(xyz[0] << 1, xyz[1] << 1, xyz[2] + 1);
		result[1] = mix(xyz[0] << 1 | 1, xyz[1] << 1, xyz[2] + 1);
		result[2] = mix(xyz[0] << 1, xyz[1] << 1 | 1, xyz[2] + 1);
		result[3] = mix(xyz[0] << 1 | 1, xyz[1] << 1 | 1, xyz[2] + 1);
		return result;
	}

	public static boolean crossedChamber(Rectangle r1, Rectangle r2) {
		return (r1.x != r2.x) || (r1.y != r2.y)
				|| ((r1.x + r1.width - 1) != (r2.x + r2.width - 1))
				|| ((r1.y + r1.height - 1) != (r2.y + r2.height - 1));
	}

	public static List<Integer> getOverlapChambers(Rectangle viewRect, int z) {

		int bound = getBound(z);

		int left = FastMath.bound(viewRect.x, 0, bound);
		int right = FastMath.bound(viewRect.x + viewRect.width - 1, 0, bound);
		int up = FastMath.bound(viewRect.y, 0, bound);
		int down = FastMath.bound(viewRect.y + viewRect.height - 1, 0, bound);

		List<Integer> result = new ArrayList<Integer>();

		for (int x = left; x <= right; x++)
			for (int y = up; y <= down; y++) {
				int fi = FoldIndexOld4.mix(x, y, z);
				if (fi >= 0)
					result.add(fi);
			}
		return result;
	}

	private static void show(Integer fi) {
		int[] exp = getXyz(fi);
		System.out.print("x=" + exp[0] + ",y=" + exp[1] + ",z=" + exp[2]
				+ "\tfi=" + fi + "\n");
	}
}
