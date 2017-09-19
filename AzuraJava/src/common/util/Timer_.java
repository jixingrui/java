package common.util;

import java.util.Timer;
import java.util.TimerTask;

public class Timer_ {
	public static void schedule(final Runnable r, long delay) {
		new Timer_().schedule_(r, delay);
	}

	// ===== class ========
	private final Timer t = new Timer();

	private TimerTask schedule_(final Runnable r, long delay) {
		final TimerTask task = new TimerTask() {
			public void run() {
				r.run();
			}
		};
		t.schedule(task, delay);
		return task;
	}
}