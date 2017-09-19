package avalon.ice4;

import com.esotericsoftware.kryo.util.IntMap;

import avalon.ice4.i.EyeI;
import common.algorithm.FoldIndex;
import common.collections.IdRecycle;
import common.thread.WorkerThread;

public class IceRoom4 {
	public final int tickDelay = 100;

	WorkerThread iceInternal = new WorkerThread("Ice4 Internal");
	WorkerThread iceOutput = new WorkerThread("Ice4 Output");

	final int chamberSize;
	final int z;

	private final IntMap<Chamber> fi_Chamber = new IntMap<>();
	private IdRecycle idBank = new IdRecycle(99, 10);

	public IceRoom4(int chamberSize, int z) {
		this.chamberSize = chamberSize;
		this.z = z;
	}

	public Body newBody(int x, int y, int angle, byte[] skin) {
		Body body = new Body(this, idBank.nextId(), angle, skin);
		iceInternal.plan(() -> {
			body.init(x, y);
		});
		return body;
	}

	public Walker newWalker(int x, int y, int angle, byte[] skin) {
		Walker walker = new Walker(this, idBank.nextId(), angle, skin);
		iceInternal.plan(() -> {
			walker.init(x, y);
		});
		return walker;
	}

	public Eye newEye(EyeI user, int x, int y, int width, int height) {
		Eye eye = new Eye(user, this, idBank.nextId());
		iceInternal.plan(() -> {
			eye.init(x, y, width, height);
		});
		return eye;
	}

	public Chamber getChamber(FoldIndex fi) {
		Chamber c = fi_Chamber.get(fi.fi);
		if (c == null) {
			c = new Chamber(fi, this);
			fi_Chamber.put(fi.fi, c);
		}
		return c;
	}

	void remove(Body body) {
		idBank.recycle(body.id);
	}

	void remove(Eye eye) {
		idBank.recycle(eye.id);
	}

}
