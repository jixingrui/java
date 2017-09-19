package common.collections.buffer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import common.collections.Returnable;

public class TimeBuffer implements IFreezerFood {

	final private static int heartRate = 40;// ms
	final private static int bufferSize = 500000;// byte

	private Freezer freezer;
	private Returnable<ZintBuffer> user;
	private volatile LockedQue que = new LockedQue();
	private AtomicInteger totalLength = new AtomicInteger();
	private AtomicBoolean servedInThisRound = new AtomicBoolean();

	public TimeBuffer(Returnable<ZintBuffer> user) {
		this.user = user;
		freezer = new Freezer(this, heartRate);
	}

	/**
	 * @param zb
	 *            length header will be written
	 */
	public void put(ZintBuffer zb) {
		que.add(zb);

//		totalLength.getAndAdd(zb.size());

		if (totalLength.get() > bufferSize * 4) {
			serve();
		}
		freezer.touch();
	}

	private boolean serve() {
		servedInThisRound.set(true);
		totalLength.set(0);
		LockedQue qNew = new LockedQue();
		LockedQue qOld = null;

		//unprotected operation. ConcurrentLinkedQueue helps.
		if (que.locked.compareAndSet(false, true)) {
			qOld = que;
			que = qNew;
		} else {
			return false;
		}

		if (qOld.isEmpty()) {
			return false;
		}

		ZintBuffer collector = new ZintBuffer();
		while (!qOld.isEmpty()) {
			ZintBuffer next = qOld.poll();
//			int nextSize = next.size();
//			if (nextSize > bufferSize) {
//				if (!collector.isEmpty()) {
//					user.return_(collector);
//					collector = new ZintBuffer();
//				}
////				next.writeZintBefore(nextSize);
//				user.return_(next);
//			} else {
//				collector.writeBytes(next.toBytes());
////				int collectorSize = collector.size();
//				if (collectorSize > bufferSize) {
//					user.return_(collector);
//					collector = new ZintBuffer();
//				}
//			}
		}

//		if (collector.size() > 0) {
//			user.return_(collector);
//		}

		return true;
	}

	@Override
	public void unfreezeHandler() {
		// Trace.trace("unfreeze");
	}

	@Override
	public boolean tryFreeze() {
		// Trace.trace("freeze");
		return que.isEmpty();
	}

	@Override
	public void heartBeatHandler() {
		// Trace.trace("alive");
		if (!servedInThisRound.get() && !que.isEmpty()) {
			// Trace.trace("heart beat serve: " + serve());
			serve();
		} else {
			// Trace.trace("heart beat serve: none");
			servedInThisRound.set(false);
		}
	}
}
