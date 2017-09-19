package common.algorithm;

public class Prime {
	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		Random rand = new Random();
//		for (int i = 0; i < 99; i++) {
//			int num = BigInteger.probablePrime(7, rand).intValue();
//			Trace.trace(num + " is prime: " + check(num));
//		}
//	}

	public static boolean check(int num) {
		int i;
		for (i = 2; i < num; i++) {
			int n = num % i;
			if (n == 0) {
				return false;
			}
		}
		return true;
	}
}
