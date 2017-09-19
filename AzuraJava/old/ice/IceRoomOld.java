package old.azura.avalon.ice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.esotericsoftware.kryo.util.IntMap;
import com.esotericsoftware.kryo.util.IntMap.Entries;

import common.algorithm.FoldIndex;
import common.collections.IdRecycle;
import common.collections.SwapList;
import old.azura.avalon.ice.i.EyeI;
import old.azura.avalon.ice.i.JumperI;
import old.azura.avalon.ice.i.RangeI;
import old.azura.avalon.ice.i.WalkerI;
import old.azura.avalon.ice.util.InOutQue;

public class IceRoomOld {
	static Logger log = Logger.getLogger(IceRoomOld.class);

	/**
	 * Read as tile side length.
	 */
	public final int chamberSize;
	private final IntMap<Chamber> fi_Chamber;
	final IntMap<Jumper> id_Zombie = new IntMap<>();
	final IdRecycle idBank = new IdRecycle(1);

	static final int moveDelay = 333;
	static final int rangeDelay = 50;
	static final int sleepDelay = 6000;

	final SwapList<Jumper> jumperQue = new SwapList<Jumper>();
	final SwapList<Eye> eyeQue = new SwapList<Eye>();
	final SwapList<Chamber> chamberQue = new SwapList<>();

	private final SwapList<RangeI> rangeQue = new SwapList<RangeI>();

	private ScheduledExecutorService icePool = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "ice");
		}
	});

	// private final ExecutorService readThread =
	// Executors.newSingleThreadExecutor();

	public IceRoomOld(int chamberSize) {
		this.chamberSize = chamberSize;
		fi_Chamber = new IntMap<>();

		poolRun();
		// threadRun();
	}

	private void poolRun() {
		// unit task
		
		icePool.scheduleWithFixedDelay(newMoveTask(), 0, moveDelay, TimeUnit.MILLISECONDS);

		// range query
		// icePool.scheduleWithFixedDelay(newRangeTask(), 0, rangeDelay,
		// TimeUnit.MILLISECONDS);

		// sleep task
		// icePool.scheduleWithFixedDelay(newSleepTask(), 0, sleepDelay,
		// TimeUnit.MILLISECONDS);
	}

	private Runnable newMoveTask() {
		return new Runnable() {

			@Override
			public void run() {
				try {

//					log.info("jumper");
					List<Jumper> sq = jumperQue.swap();
					for (Jumper z : sq) {
						z.tick();
					}

//					log.info("eye");
					List<Eye> eq = eyeQue.swap();
					for (Eye e : eq) {
						e.tock();
					}

//					log.info("chamber");
					List<Chamber> cq = chamberQue.swap();
					for (Chamber c : cq) {
						c.versionChanged.set(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private Runnable newSleepTask() {
		return new Runnable() {

			@Override
			public void run() {

				try {
					// log.debug("chambers = " + fi_Chamber.size);
					Entries<Chamber> it = fi_Chamber.entries();
					while (it.hasNext) {
						Chamber c = it.next().value;
						if (c.isIdle()) {
							it.remove();
							if (c.upper != null) {
								c.upper.lowerActive--;
							}
							// log.debug(c + " removed. " + fi_Chamber.size +
							// " left");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private Runnable newRangeTask() {
		return new Runnable() {

			@Override
			public void run() {
				try {

					// List<RangeI> rq = rangeQue.swap();
					List<RangeI> rq = null;
					for (RangeI r : rq) {
						List<Stander> stoneList = new ArrayList<Stander>();
						List<Walker> mummyList = new ArrayList<Walker>();
						for (FoldIndex fi : FoldIndex.covers(r.getRange(), r.getZ())) {
							Chamber c = fi_Chamber.get(fi.fi);
							if (c != null) {
								for (Stander s : c.standerList) {
									if (r.getRange().contains(s.x, s.y))
										stoneList.add(s);
								}
								for (Walker m : c.walkerSet) {
									if (r.getRange().contains(m.x, m.y))
										mummyList.add(m);
								}
							}
						}
						capture(r, stoneList, mummyList);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
	}

	/**
	 * write thread
	 */
	Chamber getChamber(int fi) {
		Chamber c = fi_Chamber.get(fi);
		if (c == null) {
			c = new Chamber(fi, this);
			fi_Chamber.put(fi, c);
		}
		return c;
	}

	/**
	 * unknown thread
	 */
	public Stander newStone(JumperI observer, int x, int y, int z, int angle) {
		Stander s = new Stander(this, observer, x, y, z, angle);
		s.id = idBank.nextId();
		id_Zombie.put(s.id, s);
		return s;
	}

	/**
	 * unknown thread
	 * 
	 * @param angle
	 */
	public Walker newWalker(WalkerI observer, int x, int y, int z, int angle) {
		Walker m = new Walker(this, x, y, z, angle, observer);
		m.id = idBank.nextId();
		id_Zombie.put(m.id, m);
		return m;
	}

	/**
	 * unknown thread
	 */
	public Eye newEye(EyeI user, int x, int y, int z, int width, int height) {
		return new Eye(user, this, x, y, z, width, height);
	}

	/**
	 * unknown thread
	 */
	public Jumper getZombie(int id) {
		return id_Zombie.get(id);
	}

	/**
	 * unknown thread
	 */
	public void rangeQuery(RangeI w) {
		rangeQue.add(w);
	}

	/**
	 * read thread
	 */
	public void see(EyeI observer, InOutQue<Walker> buffer) {
		// CompletableFuture.runAsync(() -> {

		// log.debug("see");

		buffer.seal();
		for (Walker m : buffer.in) {
			observer.eyeSeeIn(m);
		}
		for (Walker m : buffer.skin) {
			observer.eyeSeeChange(m);
		}
		for (Walker m : buffer.path) {
			// byte[] path = m.getPath();
			observer.eyeSeeWalking(m);
		}
		for (Walker m : buffer.out) {
			observer.eyeSeeOut(m);
		}
		buffer.clear();
		// }, readThread);
	}

	/**
	 * read thread
	 */
	public void destroy(EyeI observer) {
		// CompletableFuture.runAsync(() -> {
		observer.eyeDispose();
		// }, readThread);
	}

	/**
	 * read thread
	 */
	public void speak(EyeI observer, Jumper stone, byte[] msg) {
		// CompletableFuture.runAsync(() -> {
		observer.eyeSeeEvent(stone, msg);
		// }, readThread);
	}

	/**
	 * read thread
	 */
	private void capture(RangeI r, List<Stander> stoneList, List<Walker> mummyList) {
		log.debug("captured");
	}

	public void destroy(Jumper zombie) {
		log.info("zombie destroyed " + zombie.id);
		id_Zombie.remove(zombie.id);
		idBank.recycle(zombie.id);
		// CompletableFuture.runAsync(() -> {
		zombie.observer.jumperDispose();
		// }, readThread);
	}

	public void moved(WalkerI observer) {
		// CompletableFuture.runAsync(() -> {
		observer.walkerMoved();
		// }, readThread);
	}

}
