package azura.expresso.test;

import common.algorithm.FastMath;

public class CS {

	public ReturnDoll Receive(Holder holder) {
		final ReturnDoll ret = new ReturnDoll(FastMath.random(1, 100));

		holder.listen(new Runnable() {

			@Override
			public void run() {
				ret.fire();
			}
		});
		return ret;
	}
}
