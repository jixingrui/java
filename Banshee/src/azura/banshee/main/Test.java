package azura.banshee.main;

import java.math.RoundingMode;

import com.google.common.math.IntMath;

public class Test {

	public static void main(String[] args) {
		for (int i = -2; i < 2; i++) {
			int fx = IntMath.divide(i, 2, RoundingMode.FLOOR);
			System.out.println(i + "/2=" + fx);
		}

//		int k = IntMath.divide(-60, 256, RoundingMode.CEILING);
//		System.out.println(k);
	}

}
