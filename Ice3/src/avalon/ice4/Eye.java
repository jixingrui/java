package avalon.ice4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

import avalon.ice4.i.EyeI;
import avalon.ice4.i.SeeE;
import common.algorithm.FastMath;
import common.algorithm.FoldIndex;
import common.collections.RectB;

public class Eye {
	private static Logger log = Logger.getLogger(Eye.class);

	private IceRoom4 room;
	public final int id;
	private RectB vrDest;
	private RectB vrDestS;
	private RectB vrNow;
	private RectB vrNowS;
	private HashSet<Chamber> inChamberSet = new HashSet<>();

	final EyeI user;

	Eye(EyeI user, IceRoom4 room, int id) {
		this.user = user;
		this.room = room;
		this.id = id;
	}

	public void move(int x, int y) {
		room.iceInternal.plan(() -> {
			move_(x, y);
		});
	}

	public CompletableFuture<List<Body>> everybodyInVisual() {
		final CompletableFuture<List<Body>> future = new CompletableFuture<>();
		room.iceInternal.plan(() -> {
			ArrayList<Body> result = new ArrayList<>();
			inChamberSet.forEach(chamber -> {
				result.addAll(chamber.bodySet);
			});
			room.iceOutput.plan(() -> {
				future.complete(result);
			});
		});
		return future;
	}

	void seeBodyNew(Body body) {
		room.iceOutput.plan(() -> {
			user.eyeSee(SeeE.NEW, body);
		});
	}

	void seeBodyMove(Body body) {
		room.iceOutput.plan(() -> {
			user.eyeSee(SeeE.MOVE, body);
		});
	}

	void seeBodyStop(Body body) {
		room.iceOutput.plan(() -> {
			user.eyeSee(SeeE.STOP, body);
		});
	}

	void seeBodyPath(Walker walker) {
		room.iceOutput.plan(() -> {
			user.eyeSeePath(walker);
		});
	}

	void seeBodyChange(Body body) {
		room.iceOutput.plan(() -> {
			user.eyeSee(SeeE.SKIN, body);
		});
	}

	public void seeBodySpeak(Body body, byte[] msg) {
		room.iceOutput.plan(() -> {
			user.eyeSeeSpeak(body, msg);
		});
	}

	void seeBodyLeave(Body body) {
		int limit = Math.min(vrDest.getWidth(), vrDest.getHeight()) / 2;
		int dist = (int) FastMath.dist(vrDest.getXC(), vrDest.getYC(), body.x, body.y);
		if (dist < limit)
			throw new Error();
		room.iceOutput.plan(() -> {
			user.eyeSee(SeeE.LEAVE, body);
		});
	}

	void init(int x, int y, int width, int height) {
		vrDest = new RectB(x, y, width, height);
		vrDestS = vrDest.shrink(room.chamberSize);

		for (FoldIndex fi : FoldIndex.covers(vrDestS, room.z)) {
			Chamber chamber = room.getChamber(fi);
			chamber.addEye(this);
			inChamberSet.add(chamber);
		}

		vrNow = vrDest.clone();
		vrNowS = vrDestS.clone();
	}

	void move_(int x, int y) {
		vrDest.setXC(x);
		vrDest.setYc(y);
		vrDestS = vrDest.shrink(room.chamberSize);

		if (vrNowS.equals(vrDestS)) {
			// do nothing
		} else if (vrNowS.intersects(vrDestS) == false) {
			reloadVisual();
		} else {
			moveVisual();
		}
		vrNow = vrDest.clone();
		vrNowS = vrDestS.clone();
		// log.debug("eye move " + this.toString());
	}

	private void reloadVisual() {
		for (FoldIndex fi : FoldIndex.covers(vrNowS, room.z)) {
			Chamber chamber = room.getChamber(fi);
			chamber.removeEye(this);
		}

		for (FoldIndex fi : FoldIndex.covers(vrDestS, room.z)) {
			Chamber chamber = room.getChamber(fi);
			chamber.addEye(this);
		}
	}

	private void moveVisual() {
		RectB unionS = vrNowS.union(vrDestS);
		for (FoldIndex fi : FoldIndex.covers(unionS, room.z)) {
			boolean inOld = vrNowS.contains(fi.x, fi.y);
			boolean inNew = vrDestS.contains(fi.x, fi.y);
			if (inNew && !inOld) {
				Chamber chamber = room.getChamber(fi);
				chamber.addEye(this);
				inChamberSet.add(chamber);
			} else if (!inNew && inOld) {
				Chamber chamber = room.getChamber(fi);
				chamber.removeEye(this);
				inChamberSet.remove(this);
			}
		}
	}

	@Override
	public String toString() {
		return "<" + id + "> " + this.getClass().getSimpleName() + " xc=" + vrNow.getXC() + " yc=" + vrNow.getYC()
				+ " left=" + vrNow.getLeft() + " right=" + vrNow.getRight() + " top=" + vrNow.getTop() + " bottom="
				+ vrNow.getBottom();
	}

}
