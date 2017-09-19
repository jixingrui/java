package azura.fractale.chat;

import common.algorithm.FastMath;

public class Color {

	public static int randomBright() {
		// int color = FastMath.random(0, Integer.MAX_VALUE);
		// int seed = FastMath.random(0, 256);
		int[] rgb = new int[3];
		rgb[0] = FastMath.random(128, 256);
		rgb[1] = FastMath.random(128, 256);
		rgb[2] = FastMath.random(128, 256);
		int color = rgb[0] << 16 | rgb[1] << 8 | rgb[0];
		return color;
	}
}
