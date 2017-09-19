package common.algorithm;

import java.util.LinkedList;
import java.util.List;

public class Interleave {
	public static int up(int iv, int upLayer) {
		int oldDepth = getLayer(iv);
		int newDepth = Math.max(oldDepth - upLayer, 0);
		iv &= (0xfffffffc << ((B_MAX_DEPTH - newDepth + 1) * 2));
		iv |= newDepth;
		return iv;
	}

	public static int[] down(int iv) {
		int[] low = new int[4];
		int newDepth = getLayer(iv) + 1;
		if (newDepth == 0) {
			return null;
		} else {
			iv &= 0xfffffff0;
			for (int i = 0; i < 4; i++) {
				low[i] = iv | i << ((B_MAX_DEPTH - newDepth) * 2 + 4);
				low[i] |= newDepth;
			}
			return low;
		}
	}

	public static int downRand(int iv) {
		int tail = FastMath.random(0, 3);
		int newDepth = getLayer(iv) + 1;
		if (newDepth > 0) {
			iv &= 0xfffffff0;
			iv |= tail << ((B_MAX_DEPTH - newDepth) * 2 + 4);
			iv |= newDepth;
		}
		return iv;
	}

	public static Integer[] getNeighborArray(int iv) {
		int layer = getLayer(iv);
		int x = getX(iv);
		int y = getY(iv);
		int max = (int) Math.pow(2, layer);
		Integer[] neighbors = new Integer[9];
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++) {
				int nx = x + i;
				int ny = y + j;
				if (nx >= 0 && nx < max && ny >= 0 && ny < max) {
					neighbors[3 * i + j + 4] = mix(nx, ny, layer);
				}
			}
		return neighbors;
	}

	public static List<Integer> getNeighborList(int iv) {
		int layer = getLayer(iv);
		int x = getX(iv);
		int y = getY(iv);
		int max = (int) Math.pow(2, layer);
		List<Integer> result = new LinkedList<Integer>();
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++) {
				int nx = x + i;
				int ny = y + j;
				if (nx >= 0 && nx < max && ny >= 0 && ny < max) {
					result.add(mix(nx, ny, layer));
				}
			}
		return result;
	}

	public static byte getLayer(int mix) {
		return (byte) (mix & 0xf);
	}

	/**
	 * @param x
	 * @param y
	 * @param depth
	 *            min=0
	 * @return [y,x,depth]
	 */
	public static int mix(int x, int y, int layer)
	/*
	 * Return the interleaved code for a quadtree node at depth depth whose
	 * upper left hand corner has coordinates (x, y) in a tree with maximum
	 * depth max_depth. This function receives and returns a pointer to addr,
	 * which is either a int integer or (more typically) an array of int
	 * integers whose first integer contains the interleaved code.
	 */
	{
		// int inter=0;
		/* Scale x, y values to be consistent with maximum coord size */
		/* and depth of tree */
		// x <<= (B_MAX_DEPTH - max_depth);
		// y <<= (B_MAX_DEPTH - max_depth);
		/*
		 * calculate the bit interleaving of the x, y values that have now been
		 * appropriately shifted, and place this interleave in the address
		 * portion of addr. Note that the binary representations of x and y are
		 * being processed from right to left
		 */
		// if(depth<1) depth=1;
		// if(depth>B_MAX_DEPTH) depth=B_MAX_DEPTH;
		x <<= B_MAX_DEPTH - layer;
		y <<= B_MAX_DEPTH - layer;
		int inter = layer;
		inter |= byteval[y & 0x3][x & 0x3] << 4;
		inter |= byteval[(y >> 2) & 0xf][(x >> 2) & 0xf] << 8;
		inter |= byteval[(y >> 6) & 0xf][(x >> 6) & 0xf] << 16;
		inter |= byteval[(y >> 10) & 0xf][(x >> 10) & 0xf] << 24;
		// inter &= bytemask[B_MAX_DEPTH];
		/* if there were unused portions of the x and y addresses then */
		/* the address was too large for the depth values given. */
		/* Return address built */
		return inter;
	}

	public static short getX(int inter) {
		// int x;
		int x = xval[(inter >> 4) & 0xf];
		x |= xval[(inter >> 8) & 0xff] << 2;
		x |= xval[(inter >> 16) & 0xff] << 6;
		x |= xval[(inter >> 24) & 0xff] << 10;
		x >>= B_MAX_DEPTH - getLayer(inter);
		return (short) x;
	}

	public static short getY(int inter) {
		// int y;
		int y = yval[(inter >> 4) & 0xf];
		y |= yval[(inter >> 8) & 0xff] << 2;
		y |= yval[(inter >> 16) & 0xff] << 6;
		y |= yval[(inter >> 24) & 0xff] << 10;
		y >>= B_MAX_DEPTH - getLayer(inter);
		return (short) y;
	}

	/*
	 * The next two arrays are used in calculating the (x, y) coordinates of the
	 * upper left-hand corner of a node from its bit interleaved address. Given
	 * an 8 bit number, the arrays return the effect of removing every other bit
	 * (the y bits precede the x bits).
	 */
	static int xval[] = { 0, 1, 0, 1, 2, 3, 2, 3, 0, 1, 0, 1, 2, 3, 2, 3, 4, 5,
			4, 5, 6, 7, 6, 7, 4, 5, 4, 5, 6, 7, 6, 7, 0, 1, 0, 1, 2, 3, 2, 3,
			0, 1, 0, 1, 2, 3, 2, 3, 4, 5, 4, 5, 6, 7, 6, 7, 4, 5, 4, 5, 6, 7,
			6, 7, 8, 9, 8, 9, 10, 11, 10, 11, 8, 9, 8, 9, 10, 11, 10, 11, 12,
			13, 12, 13, 14, 15, 14, 15, 12, 13, 12, 13, 14, 15, 14, 15, 8, 9,
			8, 9, 10, 11, 10, 11, 8, 9, 8, 9, 10, 11, 10, 11, 12, 13, 12, 13,
			14, 15, 14, 15, 12, 13, 12, 13, 14, 15, 14, 15, 0, 1, 0, 1, 2, 3,
			2, 3, 0, 1, 0, 1, 2, 3, 2, 3, 4, 5, 4, 5, 6, 7, 6, 7, 4, 5, 4, 5,
			6, 7, 6, 7, 0, 1, 0, 1, 2, 3, 2, 3, 0, 1, 0, 1, 2, 3, 2, 3, 4, 5,
			4, 5, 6, 7, 6, 7, 4, 5, 4, 5, 6, 7, 6, 7, 8, 9, 8, 9, 10, 11, 10,
			11, 8, 9, 8, 9, 10, 11, 10, 11, 12, 13, 12, 13, 14, 15, 14, 15, 12,
			13, 12, 13, 14, 15, 14, 15, 8, 9, 8, 9, 10, 11, 10, 11, 8, 9, 8, 9,
			10, 11, 10, 11, 12, 13, 12, 13, 14, 15, 14, 15, 12, 13, 12, 13, 14,
			15, 14, 15 };
	static int yval[] = { 0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 0, 0,
			1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 4, 4, 5, 5, 4, 4, 5, 5,
			6, 6, 7, 7, 6, 6, 7, 7, 4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 7, 7, 6, 6,
			7, 7, 0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 0, 0, 1, 1,
			0, 0, 1, 1, 2, 2, 3, 3, 2, 2, 3, 3, 4, 4, 5, 5, 4, 4, 5, 5, 6, 6,
			7, 7, 6, 6, 7, 7, 4, 4, 5, 5, 4, 4, 5, 5, 6, 6, 7, 7, 6, 6, 7, 7,
			8, 8, 9, 9, 8, 8, 9, 9, 10, 10, 11, 11, 10, 10, 11, 11, 8, 8, 9, 9,
			8, 8, 9, 9, 10, 10, 11, 11, 10, 10, 11, 11, 12, 12, 13, 13, 12, 12,
			13, 13, 14, 14, 15, 15, 14, 14, 15, 15, 12, 12, 13, 13, 12, 12, 13,
			13, 14, 14, 15, 15, 14, 14, 15, 15, 8, 8, 9, 9, 8, 8, 9, 9, 10, 10,
			11, 11, 10, 10, 11, 11, 8, 8, 9, 9, 8, 8, 9, 9, 10, 10, 11, 11, 10,
			10, 11, 11, 12, 12, 13, 13, 12, 12, 13, 13, 14, 14, 15, 15, 14, 14,
			15, 15, 12, 12, 13, 13, 12, 12, 13, 13, 14, 14, 15, 15, 14, 14, 15,
			15 };
	/*
	 * byteval is the lookup table for coordinate interleaving. Given a 4 bit
	 * portion of the (x, y) coordinates, return the bit interleaving. Notice
	 * that this table looks like the order in which the pixels of a 16 X 16
	 * pixel image would be visited.
	 */
	static int byteval[][] = {
			{ 0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 68, 69, 80, 81, 84, 85 },
			{ 2, 3, 6, 7, 18, 19, 22, 23, 66, 67, 70, 71, 82, 83, 86, 87 },
			{ 8, 9, 12, 13, 24, 25, 28, 29, 72, 73, 76, 77, 88, 89, 92, 93 },
			{ 10, 11, 14, 15, 26, 27, 30, 31, 74, 75, 78, 79, 90, 91, 94, 95 },
			{ 32, 33, 36, 37, 48, 49, 52, 53, 96, 97, 100, 101, 112, 113, 116,
					117 },
			{ 34, 35, 38, 39, 50, 51, 54, 55, 98, 99, 102, 103, 114, 115, 118,
					119 },
			{ 40, 41, 44, 45, 56, 57, 60, 61, 104, 105, 108, 109, 120, 121,
					124, 125 },
			{ 42, 43, 46, 47, 58, 59, 62, 63, 106, 107, 110, 111, 122, 123,
					126, 127 },
			{ 128, 129, 132, 133, 144, 145, 148, 149, 192, 193, 196, 197, 208,
					209, 212, 213 },
			{ 130, 131, 134, 135, 146, 147, 150, 151, 194, 195, 198, 199, 210,
					211, 214, 215 },
			{ 136, 137, 140, 141, 152, 153, 156, 157, 200, 201, 204, 205, 216,
					217, 220, 221 },
			{ 138, 139, 142, 143, 154, 155, 158, 159, 202, 203, 206, 207, 218,
					219, 222, 223 },
			{ 160, 161, 164, 165, 176, 177, 180, 181, 224, 225, 228, 229, 240,
					241, 244, 245 },
			{ 162, 163, 166, 167, 178, 179, 182, 183, 226, 227, 230, 231, 242,
					243, 246, 247 },
			{ 168, 169, 172, 173, 184, 185, 188, 189, 232, 233, 236, 237, 248,
					249, 252, 253 },
			{ 170, 171, 174, 175, 186, 187, 190, 191, 234, 235, 238, 239, 250,
					251, 254, 255 } };
	/*
	 * bytemask is the mask for byte interleaving - masks out the
	 * non-significant bit positions. This is determined by the depth of the
	 * node. For example, a node of depth 0 is at the root. Thus, there are no
	 * branchs and no bits are significant. The bottom 4 bits (the depth) are
	 * always retained. Values are in octal notation.
	 */
	static int bytemask[] = { 0x0000000f, 0x0000003f, 0x000000ff, 0x000003ff,
			0x00000fff, 0x00003fff, 0x0000ffff, 0x0003ffff, 0x000fffff,
			0x003fffff, 0x00ffffff, 0x03ffffff, 0x0fffffff, 0x3fffffff,
			0xffffffff };
	static int B_MAX_DEPTH = 14;
}
