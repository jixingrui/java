package common.collections.timer;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import common.algorithm.FastMath;

public class TimeAxis {
	private static int cpuCount = Runtime.getRuntime().availableProcessors();
	private static ScheduledExecutorService multiThreadPool = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	private static ScheduledExecutorService[] cpu_Pool = new ScheduledExecutorService[cpuCount];
	private static AtomicLong currentTime = new AtomicLong(System.currentTimeMillis());
	private static Random random = new Random();
	static {
		Logger.getLogger(TimeAxis.class).debug("TimeAxis has thread problems, do not use!!!");

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
//				Logger.getLogger(getClass()).debug(Thread.currentThread().getName());
				currentTime.set(System.currentTimeMillis());
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
		for (int i = 0; i < cpuCount; i++) {
			cpu_Pool[i] = Executors.newSingleThreadScheduledExecutor();
		}
	}
	ScheduledExecutorService pool;

	public static TimeAxis get(int threadChannel) {
		return new TimeAxis(threadChannel);
	}

	private TimeAxis(int threadChannel) {
		int id = Math.max(0, threadChannel);
		id = Math.min(cpuCount - 1, threadChannel);
		pool = cpu_Pool[id];
	}

	public ScheduledFuture<?> schedule(Runnable work, int delay, boolean justOnce) {
		if (justOnce) {
			return pool.schedule(work, delay, TimeUnit.MILLISECONDS);
		} else {
			return pool.scheduleWithFixedDelay(work, FastMath.random(0, delay), delay - 1, TimeUnit.MILLISECONDS);
		}
	}

	public static ScheduledFuture<?> scheduleMultiThread(Runnable work, int delay) {
		return multiThreadPool.scheduleAtFixedRate(work, FastMath.random(0, delay), delay, TimeUnit.MILLISECONDS);
	}

	public static long currentTime() {
		return currentTime.get();
	}

	/**
	 * @param from
	 *            inclusive
	 * @param to
	 *            inclusive
	 */
	// public static int random(int from, int to) {
	// if (from > to)
	// return from;
	// else
	// return from + random.nextInt(to - from + 1);
	// }

	// @Test(invocationCount = 10)
	// public static void testRandom() {
	// int i = random(0, -1);
	// Trace.trace(i);
	// }

	public static byte[] random(int length) {
		byte[] result = new byte[length];
		random.nextBytes(result);
		return result;
	}

	// test helper
	private static volatile long mark;

	public static void mark() {
		mark = System.nanoTime();
	}

	public static void show(String job) {
		System.out.println("\t[" + job + "] took " + (System.nanoTime() - mark) / 1000000 + " ms");
		mark();
	}

	public static void memoryDaemon() {
		TimeAxis.get(0).schedule(new Runnable() {
			@Override
			public void run() {
				long total = Runtime.getRuntime().totalMemory();
				long free = Runtime.getRuntime().freeMemory();
				System.out.println("used memory: " + (total - free) / 1000 + " kb");
			}
		}, 1000, false);
	}

	public static long currentTimeMicro() {
		return (System.currentTimeMillis() / 100) * 100000 + (System.nanoTime() / 1000) % 100000;
	}

	public static int getTimeStamp() {
		return (int) System.nanoTime();
	}
}
