package azura.karma.run.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import azura.karma.run.Karma;

public class BeanListList implements List<Karma> {
	private ArrayList<Karma> list = new ArrayList<Karma>();
	private List<Integer> fork;

	public BeanListList(List<Integer> fork) {
		this.fork = fork;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<Karma> iterator() {
		return list.iterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(Karma e) {
		if (fork.contains(e.getType()))
			return list.add(e);
		else
			return false;
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.contains(c);
	}

	@Override
	public boolean addAll(Collection<? extends Karma> c) {
		boolean allValid = true;
		for (Karma k : c) {
			if (fork.contains(k.getType())) {
				list.add(k);
			} else {
				allValid = false;
			}
		}
		return allValid;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Karma> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Karma get(int index) {
		return list.get(index);
	}

	@Override
	public Karma set(int index, Karma element) {
		if (fork.contains(element.getType()))
			return list.set(index, element);
		else
			return null;
	}

	@Override
	public void add(int index, Karma element) {
		if (fork.contains(element.getType()))
			list.add(index, element);
	}

	@Override
	public Karma remove(int index) {
		return list.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<Karma> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Karma> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public List<Karma> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

}
