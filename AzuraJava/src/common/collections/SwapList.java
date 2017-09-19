package common.collections;

import java.util.ArrayList;
import java.util.List;

public class SwapList<T> {
	private ArrayList<T> que = new ArrayList<>();

	public void add(T item) {
		que.add(item);
	}

	public List<T> swap() {
		ArrayList<T> use = que;
		que = new ArrayList<>();
		return use;
	}
}
