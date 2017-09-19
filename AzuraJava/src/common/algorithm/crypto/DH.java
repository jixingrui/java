package common.algorithm.crypto;

import java.math.BigInteger;

import common.algorithm.FastMath;

public class DH {
	private static final int prime = 999334939;
	private static final int ground = 97;

	private final int seed = FastMath.random(1, prime + 1);
	private final int halfKey = modPow(ground, seed, prime);

	public int getHalfKey() {
		return halfKey;
	}

	public int getSharedKey(int halfKeyOther) {
		return modPow(halfKeyOther, seed, prime);
	}

	private int modPow(int root, int pow, int mod) {
		return BigInteger.valueOf(root).modPow(BigInteger.valueOf(pow), BigInteger.valueOf(mod)).intValue();
	}

	// public static void main(String[] args) {
	// TimeAxis.mark();
	// for (int i = 0; i < 10; i++) {
	// test();
	// // Trace.trace("------------");
	// }
	// TimeAxis.show("pk");
	// System.exit(0);
	// }

	// private static void test() {
	// DH alice = new DH();
	// DH bob = new DH();
	//
	// // Trace.trace("Alice.secret: " + alice.secret);
	// // Trace.trace("Bob.secret: " + bob.secret);
	//
	// int ca = alice.getHalfKey();
	// // Trace.trace("Alice.challenge: " + ca);
	// int cb = bob.getHalfKey();
	// // Trace.trace("Bob.challenge: " + cb);
	//
	// int pkB = bob.getSharedKey(ca);
	// int pkA = alice.getSharedKey(cb);
	//
	// if (pkB != pkA)
	// Trace.trace("error");
	// else
	// Trace.dot(pkA);
	// // Trace.trace("Alice.pk: " + alice.pk);
	// // Trace.trace("Bob.pk: " + bob.pk);
	// }
}
