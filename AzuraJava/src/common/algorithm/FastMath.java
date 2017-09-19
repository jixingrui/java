package common.algorithm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import common.collections.Point;
import common.logger.Trace;

/*
 * Integer Square Root function
 * Contributors include Arne Steinarson for the basic approximation idea, Dann 
 * Corbit and Mathew Hendry for the first cut at the algorithm, Lawrence Kirby 
 * for the rearrangement, improvments and range optimization, Paul Hsieh 
 * for the round-then-adjust idea, Tim Tyler, for the Java port
 * and Jeff Lawson for a bug-fix and some code to improve accuracy.
 * 
 * 
 * v0.02 - 2003/09/07
 */

/**
 * Faster replacements for (int)(java.lang.Math.sqrt(integer))
 */
public class FastMath {
	final static int[] table = { 0, 16, 22, 27, 32, 35, 39, 42, 45, 48, 50, 53, 55, 57, 59, 61, 64, 65, 67, 69, 71, 73,
			75, 76, 78, 80, 81, 83, 84, 86, 87, 89, 90, 91, 93, 94, 96, 97, 98, 99, 101, 102, 103, 104, 106, 107, 108,
			109, 110, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 128, 128, 129, 130,
			131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 144, 145, 146, 147, 148, 149, 150,
			150, 151, 152, 153, 154, 155, 155, 156, 157, 158, 159, 160, 160, 161, 162, 163, 163, 164, 165, 166, 167,
			167, 168, 169, 170, 170, 171, 172, 173, 173, 174, 175, 176, 176, 177, 178, 178, 179, 180, 181, 181, 182,
			183, 183, 184, 185, 185, 186, 187, 187, 188, 189, 189, 190, 191, 192, 192, 193, 193, 194, 195, 195, 196,
			197, 197, 198, 199, 199, 200, 201, 201, 202, 203, 203, 204, 204, 205, 206, 206, 207, 208, 208, 209, 209,
			210, 211, 211, 212, 212, 213, 214, 214, 215, 215, 216, 217, 217, 218, 218, 219, 219, 220, 221, 221, 222,
			222, 223, 224, 224, 225, 225, 226, 226, 227, 227, 228, 229, 229, 230, 230, 231, 231, 232, 232, 233, 234,
			234, 235, 235, 236, 236, 237, 237, 238, 238, 239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245,
			245, 246, 246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251, 252, 252, 253, 253, 254, 254, 255 };

	/**
	 * A faster replacement for (int)(java.lang.Math.sqrt(x)). Completely
	 * accurate for x < 2147483648 (i.e. 2^31)...
	 */
	public static int sqrt(int x) {
		int xn;

		if (x >= 0x10000) {
			if (x >= 0x1000000) {
				if (x >= 0x10000000) {
					if (x >= 0x40000000) {
						xn = table[x >> 24] << 8;
					} else {
						xn = table[x >> 22] << 7;
					}
				} else {
					if (x >= 0x4000000) {
						xn = table[x >> 20] << 6;
					} else {
						xn = table[x >> 18] << 5;
					}
				}

				xn = (xn + 1 + (x / xn)) >> 1;
				xn = (xn + 1 + (x / xn)) >> 1;
				return ((xn * xn) > x) ? --xn : xn;
			} else {
				if (x >= 0x100000) {
					if (x >= 0x400000) {
						xn = table[x >> 16] << 4;
					} else {
						xn = table[x >> 14] << 3;
					}
				} else {
					if (x >= 0x40000) {
						xn = table[x >> 12] << 2;
					} else {
						xn = table[x >> 10] << 1;
					}
				}

				xn = (xn + 1 + (x / xn)) >> 1;

				return ((xn * xn) > x) ? --xn : xn;
			}
		} else {
			if (x >= 0x100) {
				if (x >= 0x1000) {
					if (x >= 0x4000) {
						xn = (table[x >> 8]) + 1;
					} else {
						xn = (table[x >> 6] >> 1) + 1;
					}
				} else {
					if (x >= 0x400) {
						xn = (table[x >> 4] >> 2) + 1;
					} else {
						xn = (table[x >> 2] >> 3) + 1;
					}
				}

				return ((xn * xn) > x) ? --xn : xn;
			} else {
				if (x >= 0) {
					return table[x] >> 4;
				}
			}
		}

		illegalArgument();
		return -1;
	}

	private static void illegalArgument() {
		throw new IllegalArgumentException("Attemt to take the square root of negative number");
	}

	/**
	 * @param x
	 *            [0,30]
	 */
	public static int pow2x(int x) {
		if (x < 0 || x > 30)
			return 0;
		else
			return ~(-1 << x) + 1;
	}

	public static void testNumber() {
		for (int i = -3; i < 34; i++)
			Trace.trace(i + "," + pow2x(i));
	}

	/**
	 * @param n
	 *            >0
	 * @return
	 */
	public static int log2(int n) {
		if (n > 0)
			return 31 - Integer.numberOfLeadingZeros(n);
		else
			return 0;
	}

	/**
	 * @param n
	 *            >0
	 * @return
	 */
	public static int log2(long n) {
		if (n > 0)
			return 63 - Long.numberOfLeadingZeros(n);
		else
			throw new IllegalArgumentException();
	}

	// see: http://goo.gl/D9kPj
	public static int getNextPowerOfTwo(int number) {
		if (number > 0 && (number & (number - 1)) == 0)
			return number;
		else {
			int result = 1;
			while (result < number)
				result <<= 1;
			return result;
		}
	}

	public static boolean isPowerOfTwo(int x) {
		return ((x > 0) && ((x & (~x + 1)) == x));
	}

	public static int sign(int value) {
		return (value >= 0) ? 1 : -1;
	}

	public static int abs(int value) {
		return (value ^ (value >> 31)) - (value >> 31);
	}

	public static double atan(double x) {
		if (x > 1) {
			return 1.57079632675f - x / (x * x + 0.28f);
		} else if (x < -1) {
			return -1.57079632675f - x / (x * x + 0.28f);
		} else {
			return x / (1f + 0.28f * x * x);
		}
	}

	public static double atan2(double y, double x) {
		if (x > 0) {
			return atan(y / x);
		} else if (x < 0) {
			if (y >= 0) {
				return atan(y / x) + 3.1415926535f;
			}
			return atan(y / x) - 3.1415926535f;
		}

		if (y > 0) {
			return 1.57079632675f;
		}
		if (y < 0) {
			return -1.57079632675f;
		}

		return 0;
	}

	public static int atan2Degree(int y, int x) {
		double r = atan2(y, x);
		return (int) Math.toDegrees(r);
	}

	/**
	 * 
	 * @return [0,359] 0=up
	 * 
	 */
	public static int xy2Angle(int dx, int dy) {
		int angle = atan2Degree(dy, dx);
		angle = (angle - 90 + 180 + 360) % 360;
		return angle;
	}

	public static int xoy2Angle(Point p1, Point c, Point p2) {
		double p1c = sqrt(pow(c.x - p1.x) + pow(c.y - p1.y)); // p1->c (b)
		double p2c = sqrt(pow(c.x - p2.x) + pow(c.y - p2.y)); // p2->c (a)
		double p1p2 = sqrt(pow(p2.x - p1.x) + pow(p2.y - p1.y)); // p1->p2
																	// (c)
		double rad = Math.acos((p2c * p2c + p1c * p1c - p1p2 * p1p2) / (2 * p2c * p1c));
		return radian2angle(rad);
	}

	private static int pow(int v) {
		return v * v;
	}

	public static double angle2radian(int angle) {
		return Math.PI * angle / 180;
	}

	public static int radian2angle(double radian) {
		return (int) (180 * radian / Math.PI);
	}

	/**
	 * @param target
	 * @param min
	 *            inclusive
	 * @param max
	 *            exclusive
	 * @return bounded target
	 */
	public static int bound(int target, int min, int max) {
		target = Math.max(target, min);
		target = Math.min(target, max - 1);
		return target;
	}

	/**
	 * @param from
	 *            inclusive
	 * @param to
	 *            exlusive
	 * @return
	 */
	public static int random(int from, int to) {
		return ThreadLocalRandom.current().nextInt(from, to);
	}

	private static double random() {
		return ThreadLocalRandom.current().nextDouble();
	}

	public static boolean randomBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}

	public static String formatByteCount(long bytes) {
		return formatByteCount(bytes, true);
	}

	public static String formatByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static String timeSpent(long start) {
		long duration = System.currentTimeMillis() - start;
		long hr = TimeUnit.MILLISECONDS.toHours(duration);
		long min = TimeUnit.MILLISECONDS.toMinutes(duration - TimeUnit.HOURS.toMillis(hr));
		long sec = TimeUnit.MILLISECONDS
				.toSeconds(duration - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
		long ms = TimeUnit.MILLISECONDS.toMillis(duration - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min)
				- TimeUnit.SECONDS.toMillis(sec));
		return String.format("%02d:%02d:%02d.%03d", hr, min, sec, ms);
	}

	public static double pointToLineDistance(Point A, Point B, Point P) {
		double normalLength = sqrt((B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y));
		return Math.abs((P.x - A.x) * (B.y - A.y) - (P.y - A.y) * (B.x - A.x)) / normalLength;
	}

	public static double pointToLineDistance2(Point A, Point B, Point P) {
		double normalLength2 = (B.x - A.x) * (B.x - A.x) + (B.y - A.y) * (B.y - A.y);
		return Math.pow(((P.x - A.x) * (B.y - A.y) - (P.y - A.y) * (B.x - A.x)), 2) / normalLength2;
	}

	public static double dist(int x1, int y1, int x2, int y2) {
		int pow2 = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
		return sqrt(pow2);
	}

	public static double distM(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	private static final AtomicLong LAST_LONG = new AtomicLong();

	public static long tidLong() {
		long now = System.currentTimeMillis();
		while (true) {
			long lastTime = LAST_LONG.get();
			if (lastTime >= now)
				now = lastTime + 1;
			if (LAST_LONG.compareAndSet(lastTime, now))
				return now;
		}
	}

	private static final AtomicInteger LAST_INT = new AtomicInteger();
	private static final long D20170101 = 1485897309526L;

	public static int tidInt() {
		long time = System.currentTimeMillis() - D20170101;
		int now = (int) (time / 100);
		while (true) {
			int lastTime = LAST_INT.get();
			if (lastTime >= now)
				now = lastTime + 1;
			if (LAST_INT.compareAndSet(lastTime, now))
				return now;
		}
	}

	public static boolean isOdd(int value) {
		return value / 2 * 2 != value;
	}

	public static String replaceRecursive(String text, String from, String to) {
		while (true) {
			String temp = text.replace(from, to);
			if (temp.length() == text.length())
				break;
			text = temp;
		}
		return text;
	}

	private static final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	public static void schedule(int ms, Runnable task) {
		timer.schedule(task, ms, TimeUnit.SECONDS);
	}

	public static boolean isInteger(double num) {
		return num == Math.floor(num);
	}

	public static boolean similar(double d1, double d2) {
		double dif = Math.abs(d1 - d2);
		double avr = (d1 + d2) / 2;
		return dif < avr * 0.3;
	}

	// public static int randomBrightColor() {
	// int color = random(Integer.MIN_VALUE, Integer.MAX_VALUE);
	// int channel = random(0, 3);
	// int[] rgb = { 64, 64, 64 };
	// rgb[channel] = 128;
	// int mask = rgb[0] << 16 | rgb[1] << 8 | rgb[0];
	// color |= mask;
	// return color;
	// }

	public static int randomBrightColor() {
		int[] rgb = new int[3];
		rgb[0] = random(128, 255);
		rgb[1] = random(128, 255);
		rgb[2] = random(128, 255);
		int color = rgb[0] << 16 | rgb[1] << 8 | rgb[0];
		return color;
	}

	public static Point angleToXy(int angle, int r) {
		angle = (angle - 90 + 360) % 360;
		float rad = (float) angle2radian(angle);
		Point result = new Point();
		result.x = (int) (r * MathUtils.cos(rad));
		result.y = (int) (r * MathUtils.sin(rad));
		return result;
	}

	public static int ceilB(double value) {
		if (value > 0) {
			return (int) Math.ceil(value);
		} else if (value < 0) {
			return (int) Math.floor(value);
		} else {
			return 0;
		}
	}

	public static int roundByChance(double speed) {
		int i = (int) speed;
		int result = i;
		double small = speed - i;
		if (FastMath.random() < small)
			result += 1;
		return result;
	}

}
