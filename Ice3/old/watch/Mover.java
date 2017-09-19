package azura.ice.watch;

import java.util.List;

import common.collections.IdStoreI;
import common.collections.buffer.i.ZintReaderI;
import common.collections.buffer.i.ZintWriterI;

public class Mover implements IdStoreI {
	public Garden garden;
	public int id;
	int x, y;
	List<Pair> pairList;

	public void move(int x, int y) {
		pairList.forEach((pair) -> {
			pair.notifyMove();
		});
	}

	@Override
	public void writeTo(ZintWriterI writer) {
	}

	@Override
	public void readFrom(ZintReaderI reader) {
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int value) {
		id = value;
	}

	public void watch(Mover target, ComparatorE signal, int trigger, Runnable callBack) {

		Watch w = new Watch();
		w.host = this;
		w.target = target;
		w.signal = signal;
		w.triggerValue = trigger;
		w.callBack = callBack;

		Pair pair = garden.getPair(this, target);
		pair.addWatch(w);
	}
}
