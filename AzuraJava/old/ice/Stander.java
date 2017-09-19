package old.azura.avalon.ice;

import common.algorithm.FoldIndex;
import common.collections.buffer.ZintBuffer;
import old.azura.avalon.ice.i.JumperI;

public class Stander extends Jumper {

	Stander(IceRoomOld pyramid, JumperI observer, int x, int y, int z, int angle) {
		super(pyramid, observer, x, y, z, angle);
	}

	/**
	 * write thread. stone changes only notify the chamber
	 */
	@Override
	public void tick() {

		super.tick();

		// log.debug("tick");

		if (toDestroy.get()) {
			log.debug("do destroy");
			if (chamber != null) {
				chamber.removeStander(this);
				chamber = null;
			}

			room.destroy(this);
			return;
		}

		if (posChanged.compareAndSet(true, false)) {
			FoldIndex fiNew = FoldIndex.create(x / room.chamberSize, y / room.chamberSize, z);

			if (chamber == null) {// init
				log.debug("init");
				chamber = room.getChamber(fiNew.fi);
				chamber.addStander(this);
			} else if (chamber.fi.fi == fiNew.fi) {// move inside chamber
				log.debug("moved within chamber");
				chamber.change();
			} else {// cross chamber
				log.debug("moved out of chamber");
				Chamber chamberNew = room.getChamber(fiNew.fi);

				chamber.removeStander(this);
				chamberNew.addStander(this);
				chamber = chamberNew;
			}
		}

		if (skinChanged.compareAndSet(true, false)) {
			chamber.change();
		}

	}

	/*
	 * Stone.version includes position
	 */
	public byte[] toBrief() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(id);
		zb.writeZint(version);
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
