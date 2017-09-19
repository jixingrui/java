package old.azura.avalon.ice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;

public class Chamber {
	static Logger log = Logger.getLogger(Chamber.class);

	final IceRoomOld room;
	public final FoldIndex fi;
	final Chamber upper;
	final List<Stander> standerList = new ArrayList<Stander>();
	final Set<Walker> walkerSet = new HashSet<Walker>();
	final Set<Eye> observerSet = new HashSet<Eye>();

	private volatile int walkersBelow;
	// private volatile int version;
	final AtomicBoolean versionChanged = new AtomicBoolean(false);
	int lowerActive;

	Chamber(int fi, IceRoomOld pyramid) {
		this.fi = new FoldIndex(fi);
		this.room = pyramid;
		FoldIndex up = this.fi.getUp();
		if (up != null) {
			upper = pyramid.getChamber(up.fi);
			upper.lowerActive++;
		} else
			upper = null;
	}

	@Override
	public String toString() {
		return "chamber " + fi.toString();
	}

	public boolean isIdle() {
		return lowerActive == 0 && walkersBelow == 0 && standerList.size() == 0 && walkerSet.size() == 0
				&& observerSet.size() == 0;
	}

	public void addWalker(Walker mummy) {
		if (walkerSet.add(mummy) && upper != null)
			upper.plus();
	}

	private void plus() {
		walkersBelow++;
		if (upper != null)
			upper.plus();
	}

	public void removeWalker(Walker mummy) {
		if (walkerSet.remove(mummy) && upper != null)
			upper.minus();
	}

	private void minus() {
		walkersBelow--;
		if (upper != null)
			upper.minus();
	}

	/**
	 * Chamber.version is about stone
	 */
	public byte[] toBrief() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(fi.fi);
		// zb.writeZint(version);
		return zb.toBytes();
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(fi.fi);
		// zb.writeZint(version);
		zb.writeZint(standerList.size());
		for (Stander s : standerList) {
			zb.writeBytesZ(s.toBrief());
		}
		return zb.toBytes();
	}

	public void addStander(Stander stone) {
		standerList.add(stone);
		change();
	}

	public void removeStander(Stander stone) {
		standerList.remove(stone);
		change();
	}

	public void change() {
		if (versionChanged.compareAndSet(false, true)) {
			// version = ThreadLocalRandom.current().nextInt(0,
			// Integer.MAX_VALUE);
			room.chamberQue.add(this);
		}
	}

	public void addEye(Eye eye) {
		observerSet.add(eye);
		// if (versionChanged.get() == false)
		// eye.observer.eyeSeeChamberIn(this);
	}

	public void removeEye(Eye eye) {
		observerSet.remove(eye);
		// eye.observer.eyeSeeChamberOut(this);
	}
}
