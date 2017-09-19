package avalon.ice4;

import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import azura.avalon.path.PathStrider;
import common.algorithm.FastMath;
import common.collections.Point;
import common.thread.TimerOne;

public class Walker extends Body {
	public volatile int walkSpeed = 500;

	private final AtomicBoolean walking = new AtomicBoolean(false);
	public final PathStrider path;

	private ScheduledFuture<?> timer;

	public final EventDispatcher walkEvent = new EventDispatcher();

	Walker(IceRoom4 room, int id, int angle, byte[] skin) {
		super(room, id, angle, skin);
		path = new PathStrider(0, 0, angle);
	}

	public void appendTo(int xTo, int yTo) {
		if(walking.get()){
			path.append(xTo, yTo);
			chamber.eyeSet.forEach(eye -> {
				eye.seeBodyPath(this);
			});
		}else{
			restartTo(xTo, yTo);
		}
	}
	
	public void restartTo(int xTo, int yTo) {
		stopSilent();
		path.jumpStart(this.x, this.y);
		path.append(xTo, yTo);
		checkWalk();

		chamber.eyeSet.forEach(eye -> {
			eye.seeBodyPath(this);
		});

		walkEvent.dispatch("start");
	}

	public void restartPath(Queue<Point> newPath) {
		stopSilent();
		path.jumpStart(x, y);
		path.append(newPath);
		checkWalk();

		chamber.eyeSet.forEach(eye -> {
			eye.seeBodyPath(this);
		});

		walkEvent.dispatch("start");
	}

	private void checkWalk() {
		if (walking.compareAndSet(false, true)) {
			if (timer != null) {
				log.error("duplicate walking timer");
				return;
			}
			timer = TimerOne.me().scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					double speed = 1d * walkSpeed * room.tickDelay / 1000;
					int speedI = FastMath.roundByChance(speed);
					Point p = path.next(speedI);
					if (p != null) {
						angle = path.angle;
						// log.debug("walking speedI=" + speedI + ", speed=" +
						// speed);
						move_(p.x, p.y);
					} else {
						stop();
					}
				}
			}, room.tickDelay);
		}
	}

	private void stopSilent() {
		if (timer != null) {
			timer.cancel(false);
			timer = null;
		}
		path.clear();
		walking.set(false);
	}

	public void stop() {
		stopSilent();
		chamber.eyeSet.forEach(eye -> {
			eye.seeBodyStop(this);
		});
		walkEvent.dispatch("stop");
	}

}
