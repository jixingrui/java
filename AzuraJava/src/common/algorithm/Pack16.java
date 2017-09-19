package common.algorithm;

public final class Pack16 {

	public static int pack(int left, int right) {
		return (left << 16) | (right & 0xffff);
	}

	public static int extractLeft(int lr) {
		return lr >> 16;
	}

	public static int extractRight(int lr) {
		return ((lr & 0xffff) << 16) >> 16;
	}

}
