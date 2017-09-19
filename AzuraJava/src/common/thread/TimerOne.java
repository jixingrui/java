package common.thread;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TimerOne {
	private static class Holder {
		private static TimerOne instance = new TimerOne();
	}

	public static TimerOne me() {
		return Holder.instance;
	}

	private ScheduledExecutorService one = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "TimerOne");
		}
	});

	private TimerOne() {
	}

	public ScheduledFuture<?> scheduleAtFixedRate(TimerTask task, int delay) {
		return one.scheduleWithFixedDelay(task, 0, delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> schedueOnce(Runnable task, int delay) {
		return one.schedule(task, delay, TimeUnit.MILLISECONDS);
	}

}
