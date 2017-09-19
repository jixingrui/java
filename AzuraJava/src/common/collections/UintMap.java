package common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class UintMap<V> {
	int hfRemainder;
	Integer[] keyHash0_5;
	Integer[] keyHash6_;
	ArrayList<V> valueHash0_;
	Integer[] keySet;

//	public static void main(String[] args) {
//		Integer[] keys = { 7, 7, 55, 345, 0, 48, -7, -1, -5, -6, 1, 2, 4, 5, 6 };
//		UintMap<Integer> ph = new UintMap<Integer>(
//				Arrays.asList(keys));
//		for (int i : keys) {
//			System.out.println("put " + i + "\t" + ph.put(i, i) + "\tget " + i
//					+ "\t" + ph.get(i));
//		}
//		System.out.print("key set: ");
//		for (int key : ph.getKeys()) {
//			System.out.print(key + " ");
//		}
//
//		long start = System.currentTimeMillis();
//		for (int i = 0; i < 100000000; i++) {
//			// boolean b = ph.get(55) == 55;
//		}
//		System.out.println("\ntime used: "
//				+ (System.currentTimeMillis() - start));
//	}

	public static <V> UintMap<V> fromMap(Map<Integer, V> source) {
		UintMap<V> result = new UintMap<V>(source.keySet());
		for (Entry<Integer, V> entry : source.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@SuppressWarnings("unused")
	private UintMap() {
		this(new ArrayList<Integer>());
	}

	public UintMap(Collection<Integer> keySource) {
		// sort keys using TreeSet
		Set<Integer> keySet6plus = new TreeSet<Integer>(keySource);
		keyHash0_5 = new Integer[6];
		Iterator<Integer> it = keySet6plus.iterator();
		while (it.hasNext()) {
			int next = it.next();
			if (next < 0)
				it.remove();
			else if (next < 6) {
				it.remove();
				keyHash0_5[next] = next;
			}
		}
		Integer[] key6plus = keySet6plus.toArray(new Integer[0]);
		for (Integer i05 : keyHash0_5) {
			if (i05 != null)
				keySet6plus.add(i05);
		}
		this.keySet = keySet6plus.toArray(new Integer[0]);
		if (key6plus.length > 0) {
			HashFunctionDetails hfd = getHashInfo(key6plus);
			hfRemainder = hfd.remainder;
			keyHash6_ = new Integer[hfd.biggestIndex + 1];
			valueHash0_ = new ArrayList<V>(hfd.biggestIndex + 1 + 6);
			for (int idx = 0; idx < hfd.biggestIndex + 1 + 6; idx++) {
				valueHash0_.add(null);
			}
			for (int key : key6plus) {
				int index = key % hfRemainder;
				keyHash6_[index] = key;
			}
		} else {
			valueHash0_ = new ArrayList<V>(6);
			for (int idx = 0; idx < 6; idx++) {
				valueHash0_.add(null);
			}
		}
	}

	public Integer[] getKeys() {
		return keySet;
	}

	public Collection<V> getValues() {
		Collection<V> result = new ArrayList<V>(keySet.length);
		for (int key : keySet) {
			result.add(get(key));
		}
		return result;
	}

	public boolean put(Integer key, V value) {
		if (key > 5) {
			int index = key % hfRemainder;
			if (containsKey6plus(key)) {
				valueHash0_.set(index + 6, value);
				return true;
			} else {
				return false;
			}
		} else if (key >= 0) {
			if (containsKey05(key)) {
				valueHash0_.set(key, value);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public V get(int key) {
		if (key > 5) {
			if (containsKey(key)) {
				int index = key % hfRemainder;
				return valueHash0_.get(index + 6);
			} else
				return null;
		} else if (key >= 0) {
			if (containsKey05(key)) {
				return valueHash0_.get(key);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public int size() {
		return keySet.length;
	}

	public boolean containsKey(int key) {
		if (key > 5)
			return containsKey6plus(key);
		else if (key >= 0)
			return containsKey05(key);
		else
			return false;
	}

	private boolean containsKey05(Integer key) {
		return keyHash0_5[key] == key;
	}

	private boolean containsKey6plus(Integer key) {
		if (keyHash6_ == null) {
			return false;
		} else {
			int index = key % hfRemainder;
			return index < keyHash6_.length && keyHash6_[index] != null
					&& keyHash6_[index].equals(key);
		}
	}

	// static constructor
	static HashFunctionDetails reusableHashFunctionDetails = new HashFunctionDetails();
	static intHashMap cachedHashMap = new intHashMap();
	static int[] indexes;

	private static HashFunctionDetails getHashInfo(Integer[] keys) {
		indexes = new int[keys.length];
		getPerfectMap(keys);
		return reusableHashFunctionDetails;
	}

	/*
	 * Work out the perfect hash function details for this key set. Keep trying
	 * remainders until one produces a unique set.
	 */
	private static HashFunctionDetails getPerfectMap(Integer[] keys) {
		int poolSize = keys.length;
		int remainder = poolSize;
		while (!testRemainder(remainder, keys, indexes, cachedHashMap)) {
			remainder++;
		}
		int biggestKey = cachedHashMap.biggestKey();
		reusableHashFunctionDetails.remainder = remainder;
		reusableHashFunctionDetails.biggestIndex = biggestKey;
		reusableHashFunctionDetails.smallestIndex = cachedHashMap.smallestKey();
		reusableHashFunctionDetails.collectionSize = poolSize;
		reusableHashFunctionDetails.memoryFactor = ((biggestKey / poolSize) + 1);
		return reusableHashFunctionDetails;
	}

	/*
	 * Work out the perfect hash function details for this key set
	 */
	private static boolean testRemainder(int remainder, Integer[] keys,
			int[] indexes, intHashMap intmap) {
		intmap.clear();
		// Assign keys to the hash. If the assignment doesn't return a previous
		// assignment, then it is unique, otherwise it duplicates another key
		// value
		// return false.
		for (int i = 0; i < keys.length; i++) {
			indexes[i] = keys[i] % remainder;
			if (intmap.put(indexes[i], Boolean.TRUE) == Boolean.TRUE)
				return false;
		}
		return true;
	}
}

/*
 * A HashMap implementation which has int keys and Object values. For assistance
 * in working out perfect hash functions.
 */
class intHashMap {
	// Use nearest prime above 234347, which is 234361
	public intHashMap() {
		this(89, 0.75f);
	}

	protected intHashMapEntry table[];
	protected int count;
	protected int threshold;
	protected float loadFactor;

	public intHashMap(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public intHashMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Initial Capacity: "
					+ initialCapacity);
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal Load factor: "
					+ loadFactor);
		if (initialCapacity == 0)
			initialCapacity = 1;
		this.loadFactor = loadFactor;
		table = new intHashMapEntry[initialCapacity];
		threshold = (int) (initialCapacity * loadFactor);
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public int size() {
		return count;
	}

	public Object get(int key) {
		intHashMapEntry tab[] = table;
		int hash = key;
		int index = (hash & 0x7FFFFFFF) % tab.length;
		for (intHashMapEntry e = tab[index]; e != null; e = e.next)
			if ((e.hash == hash) && (key == e.key))
				return e.value;
		return null;
	}

	public Object put(int key, Object value) {
		intHashMapEntry tab[] = table;
		int hash = 0;
		int index = 0;
		// First check if the key is already in the CustomHashMap.
		// If so, replace the value.
		hash = key;
		index = (hash & 0x7FFFFFFF) % tab.length;
		for (intHashMapEntry e = tab[index]; e != null; e = e.next) {
			if ((e.hash == hash) && (key == e.key)) {
				Object old = e.value;
				e.value = value;
				return old;
			}
		}
		// Still here. Means that this is a new key.
		if (count >= threshold) {
			// Rehash the table if the threshold is exceeded
			rehash();
			tab = table;
			index = (hash & 0x7FFFFFFF) % tab.length;
		}
		// Creates the new entry.
		intHashMapEntry e = new intHashMapEntry(hash, key, value, tab[index]);
		tab[index] = e;
		count++;
		return null;
	}

	private void rehash() {
		int oldCapacity = table.length;
		intHashMapEntry oldMap[] = table;
		int newCapacity = oldCapacity * 2 + 1;
		intHashMapEntry newMap[] = new intHashMapEntry[newCapacity];
		threshold = (int) (newCapacity * loadFactor);
		table = newMap;
		for (int i = oldCapacity; i-- > 0;) {
			for (intHashMapEntry old = oldMap[i]; old != null;) {
				intHashMapEntry e = old;
				old = old.next;
				int index = (e.hash & 0x7FFFFFFF) % newCapacity;
				e.next = newMap[index];
				newMap[index] = e;
			}
		}
	}

	public void clear() {
		intHashMapEntry tab[] = table;
		for (int index = tab.length; --index >= 0;)
			tab[index] = null;
		count = 0;
	}

	public int biggestKey() {
		intHashMapEntry tab[] = table;
		int biggest = Integer.MIN_VALUE;
		for (int i = tab.length; i-- > 0;) {
			for (intHashMapEntry e = tab[i]; e != null; e = e.next) {
				if (biggest < e.key)
					biggest = e.key;
			}
		}
		return biggest;
	}

	public int smallestKey() {
		intHashMapEntry tab[] = table;
		int smallest = Integer.MAX_VALUE;
		for (int i = tab.length; i-- > 0;) {
			for (intHashMapEntry e = tab[i]; e != null; e = e.next) {
				if (smallest > e.key)
					smallest = e.key;
			}
		}
		return smallest;
	}

	public void sortKeysInto(int arr[], int startKey, int endKey) {
		intHashMapEntry tab[] = table;
		int idx = startKey;
		for (int i = tab.length; i-- > 0;) {
			for (intHashMapEntry e = tab[i]; e != null; e = e.next) {
				arr[idx++] = e.key;
			}
		}
		quicksort(arr, startKey, endKey);
	}

	public static void quicksort(int[] arr, int lo, int hi) {
		if (lo >= hi)
			return;
		int mid = (lo + hi) / 2;
		int tmp;
		int middle = arr[mid];
		if (arr[lo] > middle) {
			arr[mid] = arr[lo];
			arr[lo] = middle;
			middle = arr[mid];
		}
		if (middle > arr[hi]) {
			arr[mid] = arr[hi];
			arr[hi] = middle;
			middle = arr[mid];
			if (arr[lo] > middle) {
				arr[mid] = arr[lo];
				arr[lo] = middle;
				middle = arr[mid];
			}
		}
		int left = lo + 1;
		int right = hi - 1;
		if (left >= right)
			return;
		for (;;) {
			while (arr[right] > middle) {
				right--;
			}
			while (left < right && arr[left] <= middle) {
				left++;
			}
			if (left < right) {
				tmp = arr[left];
				arr[left] = arr[right];
				arr[right] = tmp;
				right--;
			} else {
				break;
			}
		}
		quicksort(arr, lo, left);
		quicksort(arr, left + 1, hi);
	}
}

class intHashMapEntry {
	int hash;
	int key;
	Object value;
	intHashMapEntry next;

	protected intHashMapEntry(int hash, int key, Object value,
			intHashMapEntry next) {
		this.hash = hash;
		this.key = key;
		this.value = value;
		this.next = next;
	}
}

/*
 * Internal class to provide memory store for the various values generated when
 * trying to determine a hash function for a particular set of keys.
 */
class HashFunctionDetails {
	public int remainder;
	public int biggestIndex;
	public int smallestIndex;
	public int collectionSize;
	public int memoryFactor;
	public int divisor;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("remainder: ").append(remainder);
		sb.append(" biggestIndex: ").append(biggestIndex);
		sb.append(" smallestIndex: ").append(smallestIndex);
		sb.append(" collectionSize: ").append(collectionSize);
		sb.append(" memoryFactor: ").append(memoryFactor);
		sb.append(" divisor: ").append(divisor);
		return sb.toString();
	}
}