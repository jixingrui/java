package common.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerThread extends Thread {
	private final LinkedBlockingQueue<Runnable> que;
	private final AtomicBoolean running = new AtomicBoolean(true);

	public WorkerThread(String name) {
		super(name);
		this.que = new LinkedBlockingQueue<>();
		this.start();
	}

	public void kill() {
		plan(() -> running.set(false));
	}

//	public void planAsync(Runnable task) {
//		CompletableFuture.runAsync(() -> plan(task));
//	}

	public void plan(Runnable task) {
		try {
			que.put(task);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				Runnable task = que.take();
				task.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
