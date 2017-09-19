package old.azura.avalon.ice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import common.algorithm.FoldIndex;
import common.collections.RectB;
import old.azura.avalon.ice.i.EyeI;
import old.azura.avalon.ice.util.ChangeTypeE;
import old.azura.avalon.ice.util.InOutQue;

public class Eye {
	static Logger log = Logger.getLogger(Eye.class);

	public final EyeI observer;
	private final IceRoomOld room;

	private volatile int zOld, z;

	/**
	 * null means to initialize
	 */
	private volatile RectB vrNow, vrNowS;
	/**
	 * null means to destroy
	 */
	private volatile RectB vrDest, vrDestS;

	private final AtomicBoolean inQue = new AtomicBoolean();

	private InOutQue<Walker> buffer = new InOutQue<Walker>();

	private HashSet<Chamber> inChamberSet = new HashSet<>();

	public Eye(EyeI user, IceRoomOld pyramid, int x, int y, int z, int width, int height) {
		this.observer = user;
		this.room = pyramid;
		this.z = z;
		this.zOld = z;
		vrDest = new RectB(x, y, width, height);
		vrDestS = vrDest.shrink(pyramid.chamberSize);
		// log.debug("eye create " + vrDest.toString());
		enque();
	}

	/**
	 * unknown thread
	 */
	public void move(int x, int y, int z) {
		// log.debug("eye move x=" + x + ",y=" + y);
		if (vrDest != null) {
			this.z = z;
			vrDest.setXC(x);
			vrDest.setYc(y);
			vrDestS = vrDest.shrink(room.chamberSize);
			enque();
		} else {
			log.error("should not move after destroy");
		}
	}

	/**
	 * unknown thread
	 */
	public void destroyFuture() {
		vrDest = null;
		vrDestS = null;
		enque();
	}

	/**
	 * unknown thread
	 */
	protected void enque() {
		if (inQue.compareAndSet(false, true)) {
			room.eyeQue.add(this);
		} else {
			// log.debug("eye enque dup");
		}
	}

	void tock() {

		inQue.set(false);

		// log.debug("tock");

		if (vrDest == null) {// destroy
			log.debug("destroy");
			if (vrNowS != null) {
				// RectB nowS = vrNow.shrink(pyramid.chamberSize);

				for (FoldIndex fi : FoldIndex.covers(vrNowS, z)) {
					room.getChamber(fi.fi).removeEye(this);
				}
			} else
				log.warn("destroyed before initialize");
			room.destroy(observer);
		} else if (vrNow == null) {// init
			// log.debug("init");
			// vrDestS = vrDest.shrink(pyramid.chamberSize);
			for (FoldIndex fi : FoldIndex.covers(vrDestS, z)) {
				// log.debug("enter chamber " + fi.toString());
				Chamber chamber = room.getChamber(fi.fi);
				chamber.addEye(this);
				buffer.inAll(chamber.walkerSet);
				addChamber(chamber);
			}
		} else if (zOld != z) {
			log.debug("reload visual by z");
			reloadVisual();
		} else if (vrNowS.equals(vrDestS)) {
			// log.debug("do nothing by vrNowS==vrDestS");
			// do nothing
		} else if (vrNowS.intersects(vrDestS) == false) {
			log.debug("reload visual by vr no intersection");
			reloadVisual();
		} else {
			// log.debug("eye chamber change: from " + vrNowS.toString() + " to
			// " + vrDestS.toString());
			move();
		}

		vrNow = vrDest.clone();
		vrNowS = vrDestS.clone();

		if (!buffer.isEmpty() && isActive()) {
			// log.debug("buffer see");
			room.see(observer, buffer);
			buffer = new InOutQue<>();
		}
	}

	private void addChamber(Chamber chamber) {
		// if (inChamberSet.contains(chamber) == true)
		// throw new Error();
		inChamberSet.add(chamber);
	}

	private void removeChamber(Chamber chamber) {
		// if (inChamberSet.contains(chamber) == false)
		// throw new Error();
		inChamberSet.remove(chamber);
	}

	/**
	 * eye position changed
	 */
	private void move() {
		// log.debug("chamber change");

		// RectB union = vrNow.union(vrDest);
		// RectB unionS = union.shrink(pyramid.chamberSize);
		RectB unionS = vrNowS.union(vrDestS);
		for (FoldIndex fi : FoldIndex.covers(unionS, z)) {

			boolean inOld = vrNowS.contains(fi.x, fi.y);
			boolean inNew = vrDestS.contains(fi.x, fi.y);

			if (inNew && !inOld) {
				// log.debug("chamber in " + fi);
				Chamber chamber = room.getChamber(fi.fi);
				chamber.addEye(this);
				buffer.inAll(chamber.walkerSet);
				addChamber(chamber);
			} else if (!inNew && inOld) {
				// log.debug("chamber out " + fi);
				Chamber chamber = room.getChamber(fi.fi);
				chamber.removeEye(this);
				buffer.outAll(chamber.walkerSet);
				removeChamber(chamber);
			} else if (inNew && inOld) {
				// log.debug("chamber remain " + fi);
			} else {
				// log.debug("chamber uninvolved " + fi);
			}
		}
	}

	private void reloadVisual() {
		// RectB vrNowS = vrNow.shrink(pyramid.chamberSize);
		// RectB vrDestS = vrDest.shrink(pyramid.chamberSize);

		for (FoldIndex fi : FoldIndex.covers(vrNowS, zOld)) {
			Chamber chamber = room.getChamber(fi.fi);
			chamber.removeEye(this);
			buffer.outAll(chamber.walkerSet);
		}

		for (FoldIndex fi : FoldIndex.covers(vrDestS, z)) {
			Chamber chamber = room.getChamber(fi.fi);
			chamber.addEye(this);
			buffer.inAll(chamber.walkerSet);
		}
	}

	public boolean isActive() {
		return vrDest != null;
	}

	/**
	 * stone jumped into visual or requesting path sync
	 */
	void seeMoveIn(Walker mummy) {
		// log.debug("see move-in " + mummy.toString());
		buffer.change(mummy, ChangeTypeE.in);
		enque();
	}

	/**
	 * mummy modified shape
	 */
	void seeChange(Walker mummy) {
		// log.debug("see change " + mummy.toString());
		buffer.change(mummy, ChangeTypeE.skin);
		enque();
	}

	/**
	 * mummy jumped out of visual
	 */
	void seeMoveOut(Walker mummy) {
		// log.debug("see move-out " + mummy.toString()+" by "+this);
		buffer.change(mummy, ChangeTypeE.out);
		enque();
	}

	/**
	 * mummy moved along a path
	 */
	public void seePath(Walker mummy) {
		buffer.change(mummy, ChangeTypeE.path);
		enque();
	}

	public List<Walker> findBodyInVisual() {
		ArrayList<Walker> result = new ArrayList<>();
		for (FoldIndex fi : FoldIndex.covers(vrNowS, z)) {
			Chamber chamber = room.getChamber(fi.fi);
			result.addAll(chamber.walkerSet);
		}
		return result;
	}

}
