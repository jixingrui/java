package old.azura.avalon.ice;

import java.awt.Point;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import azura.avalon.path.PathStrider;
import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;
import old.azura.avalon.ice.i.WalkerI;

public class Walker extends Jumper {
	static Logger log = Logger.getLogger(Walker.class);

	/**
	 * pixels per second
	 */
	public volatile int walkSpeed = 120;

	private final PathStrider path;
	final AtomicBoolean pathChanged = new AtomicBoolean();

	public Object cargo;

	Walker(IceRoomOld pyramid, int x, int y, int z, int angle, WalkerI observer) {
		super(pyramid, observer, x, y, z, angle);
		path = new PathStrider(x, y, angle);
	}

	/**
	 * unknown thread
	 */
	public void appendPath(Queue<Point> path_) {
		// log.debug("path length=" + path_.size());
		path.append(path_);
		pathChanged.set(true);
		super.enque();
	}

	public void stop() {
		if (path.isEmpty())
			return;

		path.clear();
		// pathChanged.set(true);
		// log.info("walker at " + x + "," + y);
	}

	@Override
	public void tick() {

		super.tick();

//		 log.debug("tick");

		if (toDestroy.get()) {
			// log.debug("do destroy");
			if (chamber != null) {
				chamber.removeWalker(this);
				for (Eye eye : chamber.observerSet) {
					eye.seeMoveOut(this);
				}
				chamber = null;
			}
			room.destroy(this);
			// pyramid.id_Zombie.remove(this.id);
			return;
		}

		if (path == null)
			throw new Error();

		Point next = path.next(walkSpeed * IceRoomOld.moveDelay / 1000);
		if (next != null) {
			angle = path.angle;
			// log.debug("step " + next.x + "," + next.y + "," + angle);
			super.jump(next.x, next.y, super.z);
		}

		if (posChanged.compareAndSet(true, false)) {
			// int xs=(int) Math.floor(1.0*x/pyramid.chamberSize);
			int xs = Math.floorDiv(x, room.chamberSize);
			int ys = Math.floorDiv(y, room.chamberSize);
			FoldIndex fiNew = FoldIndex.create(xs, ys, z);
			// FoldIndex fiNew = FoldIndex.create(x / pyramid.chamberSize, y /
			// pyramid.chamberSize, z);

			if (chamber == null) {// init
				// log.debug("init");
				chamber = room.getChamber(fiNew.fi);
				chamber.addWalker(this);
				for (Eye eye : chamber.observerSet) {
					eye.seeMoveIn(this);
				}
			} else if (chamber.fi.fi == fiNew.fi) {// move within chamber
				// log.debug("walker moved within chamber: " +
				// fiNew.toString());
			} else {// cross chamber
//				log.debug("walker moved across chamber: from " + chamber.fi.toString() + " to " + fiNew.toString());

				Chamber chamberOld = this.chamber;
				Chamber chamberNew = room.getChamber(fiNew.fi);

				if (chamberNew.observerSet.isEmpty())
					log.error("possible leave own sight");

				chamber.removeWalker(this);
				chamberNew.addWalker(this);
				chamber = chamberNew;

				HashSet<Eye> oldSet = new HashSet<Eye>(chamberOld.observerSet);
				oldSet.removeAll(chamberNew.observerSet);
				for (Eye eye : oldSet) {
					eye.seeMoveOut(this);
				}
				HashSet<Eye> newSet = new HashSet<Eye>(chamberNew.observerSet);
				newSet.removeAll(chamberOld.observerSet);
				for (Eye eye : newSet) {
					eye.seeMoveIn(this);
					if (path.isEmpty() == false)
						eye.seePath(this);
				}
			}

			room.moved((WalkerI) observer);
		}

		if (pathChanged.compareAndSet(true, false)) {
			for (Eye eye : chamber.observerSet) {
				eye.seePath(this);
			}
		} else if (skinChanged.compareAndSet(true, false)) {
			for (Eye eye : chamber.observerSet) {
				eye.seeChange(this);
			}
		}

	}

	public byte[] getPath() {
		return path.toBytes();
	}

	/*
	 * Mummy.version does not include position
	 */
	public byte[] toBrief() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(id);
		zb.writeZint(version);
		zb.writeZint(x);
		zb.writeZint(y);
		zb.writeZint(z);
		zb.writeZint(angle);
		return zb.toBytes();
	}


	// @Override
	// public void readFrom(ZintReaderI reader) {
	// }
	//
	// @Override
	// public void writeTo(ZintWriterI writer) {
	// }
}
