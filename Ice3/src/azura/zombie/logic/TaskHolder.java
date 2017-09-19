package azura.zombie.logic;

import java.util.concurrent.ScheduledFuture;

import common.thread.TimerOne;

public class TaskHolder implements Runnable {

	private final int delay;
	private volatile Runnable task;
	private ScheduledFuture<?> timer;

	public TaskHolder(int delay) {
		this.delay = delay;
	}

	public boolean isEmpty() {
		return task == null;
	}

	public void addTask(Runnable task) {
		if (this.task != null || task == null)
			throw new Error();

		this.task = task;
		if (timer == null)
			timer = TimerOne.me().schedueOnce(this, delay);
	}

	@Override
	public void run() {
		task.run();
		task = null;
		timer = null;
	}

	public void clearTask() {
		task = null;
		if (timer != null) {
			timer.cancel(false);
			timer = null;
		}
	}

}
