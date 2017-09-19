package old.azura.avalon.ice.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class InOutQue<T> {

	private List<IoQueHolder<T>> que = new ArrayList<IoQueHolder<T>>();
	public HashSet<T> in;
	public HashSet<T> out;
	public HashSet<T> path;
	public HashSet<T> skin;

	public boolean isEmpty() {
		return que.isEmpty();
	}

	public void change(T item, ChangeTypeE type) {
		IoQueHolder<T> h = new IoQueHolder<T>();
		h.changeType = type;
		h.item = item;
		que.add(h);
	}

	public void inAll(Collection<? extends T> list) {
		for (T item : list) {
			change(item, ChangeTypeE.in);
		}
	}

	public void outAll(Collection<? extends T> list) {
		for (T item : list) {
			change(item, ChangeTypeE.out);
		}
	}

	public void seal() {
		in = new HashSet<>();
		skin = new HashSet<>();
		path = new HashSet<>();
		out = new HashSet<>();
		for (IoQueHolder<T> h : que) {
			if (h.changeType == ChangeTypeE.in) {
				in.add(h.item);
			} else if (h.changeType == ChangeTypeE.out) {
				out.add(h.item);
			} else if (h.changeType == ChangeTypeE.skin) {
				skin.add(h.item);
			} else if (h.changeType == ChangeTypeE.path) {
				path.add(h.item);
			}
		}
		in.removeAll(out);
		skin.removeAll(in);
		path.removeAll(out);
		que.clear();
	}

	public void clear() {
		in = null;
		out = null;
		path = null;
		skin = null;
	}
}

class IoQueHolder<T> {
	ChangeTypeE changeType;
	T item;
}