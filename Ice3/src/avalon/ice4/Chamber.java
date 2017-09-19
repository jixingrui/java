package avalon.ice4;

import java.util.HashSet;

import common.algorithm.FoldIndex;

public class Chamber {

	private final IceRoom4 room;
	public final FoldIndex fi;
	public HashSet<Body> bodySet = new HashSet<>();
	public HashSet<Eye> eyeSet = new HashSet<>();

	public Chamber(FoldIndex fi, IceRoom4 room) {
		this.fi = fi;
		this.room = room;
	}

	public void addBody(Body body) {
		bodySet.add(body);
	}

	public void removeBody(Body body) {
		bodySet.remove(body);
	}

	public void addEye(Eye eye) {
		eyeSet.add(eye);
		bodySet.forEach(body -> {
			eye.seeBodyNew(body);
		});
	}

	public void removeEye(Eye eye) {
		eyeSet.remove(eye);
		bodySet.forEach(body -> {
			eye.seeBodyLeave(body);
		});
	}

	public String toString() {
		return fi.toString();
	};
}
