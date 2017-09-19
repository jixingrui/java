package azura.helios.hard10;

import java.util.TreeMap;

import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class TreeTrain implements BytesI {
	// public ArrayList<TreeCar> train = new ArrayList<>();
	public TreeMap<Integer, TreeCar> train = new TreeMap<Integer, TreeCar>();

	@Override
	public void fromBytes(byte[] bytes) {
		train.clear();

		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			TreeCar car = new TreeCar();
			car.readFrom(zb);
			// train.add(car);
			train.put(car.idCar, car);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(train.size());
		for (TreeCar car : train.values()) {
			car.writeTo(zb);
		}
		return zb.toBytes();
	}

	public void add(int id, byte[] data) {
		TreeCar car = new TreeCar();
		car.idCar = id;
		car.data = data;
		// train.add(car);
		train.put(id, car);
	}

	public void adopt(int idParent, int idChild) {
		TreeCar parent = train.get(idParent);
		parent.childList.add(idChild);

		TreeCar child = train.get(idChild);
		child.parent = idParent;
	}

}
