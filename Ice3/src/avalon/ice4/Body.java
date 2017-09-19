package avalon.ice4;

import java.util.HashSet;

import org.apache.log4j.Logger;

import common.algorithm.FastMath;
import common.algorithm.FoldIndex;

public class Body {
	static Logger log = Logger.getLogger(Body.class);

	protected IceRoom4 room;
	public final int id;

	public volatile int x, y, angle;
	public volatile byte[] skin;
	protected Chamber chamber;

	public Object cargo;

	Body(IceRoom4 room, int id, int angle, byte[] skin) {
		this.room = room;
		this.id = id;
		this.angle = angle;
		this.skin = skin;
	}

	// ========== public ===============

	public void setSkin(byte[] bytes) {
		room.iceInternal.plan(() -> {
			this.skin = bytes;
			for (Eye eye : chamber.eyeSet) {
				eye.seeBodyChange(this);
			}
		});
	}

	public void speak(byte[] msg) {
		room.iceInternal.plan(() -> {
			for (Eye eye : chamber.eyeSet) {
				eye.seeBodySpeak(this, msg);
			}
		});
	}

	@Override
	public String toString() {
		return "<" + id + "> " + this.getClass().getSimpleName() + " (" + x + "," + y + ")";
	}

	// ================== internal ===============
	void init(int x, int y) {
		this.x = x;
		this.y = y;
		FoldIndex fi = getFi();

		chamber = room.getChamber(fi);
		chamber.addBody(this);
		for (Eye eye : chamber.eyeSet) {
			eye.seeBodyNew(this);
		}
	}

	public void move(int x, int y) {
		room.iceInternal.plan(() -> {
			move_(x, y);
		});
	}

	void move_(int x, int y) {
		// int dist = (int) FastMath.dist(x, y, this.x, this.y);
		// if (cargo instanceof Zombie)
		// log.debug("zombie move dist=" + dist+" "+toString());
		// log.debug("body move " + this.toString());

		this.angle = FastMath.xy2Angle(x - this.x, y - this.y);

		this.x = x;
		this.y = y;
		FoldIndex fi = getFi();

		if (chamber.fi.equals(fi) == false) {

			Chamber chamberOld = this.chamber;
			Chamber chamberNew = room.getChamber(fi);
			chamber = chamberNew;

			chamberOld.removeBody(this);
			chamberNew.addBody(this);

			HashSet<Eye> oldSet = new HashSet<Eye>(chamberOld.eyeSet);
			oldSet.removeAll(chamberNew.eyeSet);
			for (Eye eye : oldSet) {
				eye.seeBodyLeave(this);
			}
			HashSet<Eye> newSet = new HashSet<Eye>(chamberNew.eyeSet);
			newSet.removeAll(chamberOld.eyeSet);
			for (Eye eye : newSet) {
				eye.seeBodyNew(this);
			}
		} else {
			chamber.eyeSet.forEach(eye -> {
				eye.seeBodyMove(this);
			});
		}
	}

	private FoldIndex getFi() {
		int xs = Math.floorDiv(x, room.chamberSize);
		int ys = Math.floorDiv(y, room.chamberSize);
		FoldIndex fiNew = FoldIndex.create(xs, ys, room.z);
		return fiNew;
	}

	void dispose() {
		chamber.removeBody(this);
		chamber = null;
		room.remove(this);
		room = null;
	}

}
