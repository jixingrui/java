package azura.expresso.ns;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Family {
	HashMap<Integer, Set<Integer>> parent_ChildSet = new HashMap<Integer, Set<Integer>>();
	HashMap<Integer, Set<Integer>> child_ParentSet = new HashMap<Integer, Set<Integer>>();
	Set<Integer> unitSet = new HashSet<Integer>();

//	public static void main(String[] args) {
//		Family family = new Family();
//		family.link(1, 2);
//		family.link(2, 3);
//		family.link(3, 2);
//		// family.delink(1, 2);
//
//		showSet(family.getParentSet(3));
//		showSet(family.getAncestorSet(3));
//	}

//	private static void showSet(Set<Integer> set) {
//		System.out.println();
//		for (int i : set) {
//			System.out.print(i + ",");
//		}
//	}

	public void clear() {
		unitSet.clear();
		parent_ChildSet.clear();
		child_ParentSet.clear();
	}

	public void add(int id) {
		unitSet.add(id);
	}

	public Collection<Integer> link(int parent, int child) {

		Set<Integer> caSet = getAncestorSet(child);
		Set<Integer> paSet = getAncestorSet(parent);
		caSet.retainAll(paSet);

		for (int a : caSet) {
			delink(a, child);
		}
		getChildSet(parent).add(child);
		getParentSet(child).add(parent);

		return caSet;
	}

	public void delink(int parent, int child) {
		Set<Integer> sc = getChildSet(parent);
		if (sc.remove(child)) {
			if (sc.size() == 0)
				parent_ChildSet.remove(parent);

			Set<Integer> sp = getParentSet(child);
			sp.remove(parent);
			if (sp.size() == 0)
				child_ParentSet.remove(child);
		}
	}

	public Set<Integer> getChildSet(int parent) {
		Set<Integer> s = parent_ChildSet.get(parent);
		if (s == null) {
			s = new HashSet<Integer>();
			parent_ChildSet.put(parent, s);
		}
		return s;
	}

	public Set<Integer> getParentSet(int child) {
		Set<Integer> s = child_ParentSet.get(child);
		if (s == null) {
			s = new HashSet<Integer>();
			child_ParentSet.put(child, s);
		}
		return s;
	}

	public Set<Integer> getAncestorSet(int id) {
		Set<Integer> finishSet = new HashSet<Integer>();
		LinkedList<Integer> unfinish = new LinkedList<Integer>();
		unfinish.add(id);
		while (unfinish.size() > 0) {
			int current = unfinish.pop();
			finishSet.add(current);
			for (int parent : getParentSet(current)) {
				if (!finishSet.contains(parent)) {
					unfinish.add(parent);
				}
			}
		}
		finishSet.remove(id);
		return finishSet;
	}

	public Set<Integer> getDescendentSet(int id) {
		Set<Integer> finishSet = new HashSet<Integer>();
		LinkedList<Integer> unfinish = new LinkedList<Integer>();
		unfinish.add(id);
		while (unfinish.size() > 0) {
			int current = unfinish.pop();
			finishSet.add(current);
			for (int child : getChildSet(current)) {
				if (!finishSet.contains(child)) {
					unfinish.add(child);
				}
			}
		}
		finishSet.remove(id);
		return finishSet;
	}

	public Set<Integer> getAll() {
		return new HashSet<Integer>(unitSet);
	}
}
