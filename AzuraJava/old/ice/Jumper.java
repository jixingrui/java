package old.azura.avalon.ice;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import common.algorithm.FastMath;
import common.collections.buffer.ZintBuffer;
import old.azura.avalon.ice.i.JumperI;

public abstract class Jumper {
	protected static Logger log = Logger.getLogger(Jumper.class);

	private final AtomicBoolean inQue = new AtomicBoolean();

	protected final AtomicBoolean posChanged = new AtomicBoolean(true);
	protected final AtomicBoolean skinChanged = new AtomicBoolean();
	protected final AtomicBoolean toDestroy = new AtomicBoolean();

	/**
	 * @chamber==null uninitialized
	 */
	protected volatile Chamber chamber;
	protected final IceRoomOld room;
	volatile int id = -1;
	protected volatile int version;
	public volatile int x, y, z;
	public volatile int angle;
	private volatile byte[] skin;
	protected final JumperI observer;

	Jumper(IceRoomOld room, JumperI observer, int x, int y, int z, int angle) {
		this.room = room;
		this.observer = observer;
		this.angle = angle;
		jump(x, y, z);
	}

	protected void tick() {
		inQue.set(false);
	}

	/**
	 * unknown thread
	 */
	public void destroyFuture() {
		// log.debug("destroy requested");
		toDestroy.set(true);
		enque();
	}

	/**
	 * unknown thread
	 */
	protected void enque() {
		if (inQue.compareAndSet(false, true)) {
			room.jumperQue.add(this);
			// log.debug("enque");
		} else {
			// log.debug("enque dup");
		}
	}

	/**
	 * write thread
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * write thread
	 */
	public byte[] getSkin() {
		return skin;
	}

	/**
	 * unknown thread
	 */
	public void setSkin(byte[] skin) {
		if (skin == null)
			log.warn("Unit: skin is set to null");
		this.skin = skin;
		version = FastMath.random(0, Integer.MAX_VALUE);
		skinChanged.set(true);
		enque();
	}

	/**
	 * unknown thread
	 */
	public void speak(byte[] msg) {
		log.debug("speak");
		for (Eye eye : chamber.observerSet) {
			room.speak(eye.observer, this, msg);
		}
	}

	/**
	 * unknown thread
	 */
	public void turnTo(int angle) {
		log.info("turn to " + angle);
		this.angle = angle;
		skinChanged.set(true);
		enque();
	}

	/**
	 * unknown thread
	 */
	public void jump(int x, int y, int z) {
		// log.debug("jump " + x + "," + y);
		this.x = x;
		this.y = y;
		this.z = z;
		posChanged.set(true);
		enque();
	}

	public int getId() {
		return id;
	}
	// @Override
	// public int getId() {
	// return id;
	// }
	//
	// @Override
	// public void setId(int value) {
	// // log.info("id=" + value);
	// if (id >= 0)
	// throw new Error("Unit: cannot assign id twice");
	// if (value < 0)
	// throw new Error("Unit: cannot assign negative id");
	// id = value;
	// }

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "<" + id + "> " + this.getClass().getSimpleName() + " (" + x + "," + y + ")";
	}

	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(id);
		zb.writeZint(version);
		zb.writeZint(x);
		zb.writeZint(y);
		zb.writeZint(z);
		zb.writeZint(angle);
		zb.writeBytesZ(skin);
		return zb.toBytes();
	}

	// public void moveFinish() {
	// observer.jumperMoveFinish();
	// }

}
