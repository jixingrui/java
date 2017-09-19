package azura.ice.watch;

import java.util.HashMap;

import common.collections.IdStore;

public class Garden {

	IdStore<Mover> id_Mover;
	HashMap<Integer, Pair> id_Pair;

	public Mover newMover() {
		Mover mover = new Mover();
		id_Mover.add(mover);
		return mover;
	}

	public Mover getMover(int id) {
		return id_Mover.get(id);
	}

	public void deleteMover(int id) {
		id_Mover.remove(id);
	}

	public Pair getPair(Mover one, Mover two) {
		Pair pair = new Pair(one, two);
		Pair oldPair = id_Pair.get(pair.getId());
		if (oldPair == null) {
			id_Pair.put(pair.getId(), pair);
			oldPair = pair;
		}
		return pair;
	}

}
