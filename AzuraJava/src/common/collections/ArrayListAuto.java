package common.collections;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ArrayListAuto<E> extends ArrayList<E> {

	@Override
	public E set(int idx, E value) {
		expand(idx);
		return super.set(idx, value);
	}

	@Override
	public E get(int idx) {
		if (idx >= 0 && idx < size())
			return super.get(idx);
		else
			return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		if (fromIndex < 0 || toIndex > size() || fromIndex > toIndex)
			return null;
		else {
			List<E> sub = super.subList(fromIndex, toIndex);
			try {
				@SuppressWarnings("unchecked")
				ArrayListAuto<E> result = ArrayListAuto.class.newInstance();
				result.addAll(sub);
				return result;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	private void expand(int idx) {
		if (idx >= super.size()) {
			int i = super.size() - 1;
			while (i < idx) {
				super.add(null);
				i++;
			}
		}
	}
}
