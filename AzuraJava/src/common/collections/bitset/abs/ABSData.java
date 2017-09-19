package common.collections.bitset.abs;

public class ABSData implements Comparable<ABSData> {
	final int pos;
	int forward;

	ABSData(int pos) {
		this.pos = pos;
	}

	@Override
	public int compareTo(ABSData o) {
		if (this.pos > o.pos)
			return 1;
		else if (this.pos < o.pos)
			return -1;
		else
			return 0;
	}
}
