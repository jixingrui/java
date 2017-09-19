package common.collections.timer;

import java.util.concurrent.ScheduledFuture;


public class Freezer implements Runnable {
	volatile private boolean frozen = true, touched = false;
	final private IFreezerFood food;
	private int checkDelay;
	private ScheduledFuture<?> task;

	/**
	 * start frozen
	 */
	public Freezer(IFreezerFood food, int checkDelay) {
		this.food = food;
		this.checkDelay = checkDelay;
	}

	@Override
	public void run() {
		food.heartBeatHandler();
		if (!touched) {
			synchronized (this) {
				frozen = food.tryFreeze();
				if (frozen) {
					task.cancel(false);
					task = null;
				}
			}
		} else {
			touched = false;
		}
	}

	public void touch() {
		touched = true;
		if (frozen) {
			food.unfreezeHandler();
			frozen = false;
		}
		if (task == null || task.isCancelled())
			task = TimeAxis.scheduleMultiThread(this, checkDelay);
	}
}
