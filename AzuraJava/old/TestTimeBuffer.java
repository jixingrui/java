package common.collections.buffer;

import common.algorithm.FastMath;
import common.collections.Returnable;
import common.collections.timer.TimeAxis;

public class TestTimeBuffer {

	private static TimeBuffer tb;
	private static int size = 0;

	public static void main(String[] args) {
		tb = new TimeBuffer(new Returnable<ZintBuffer>() {

			@Override
			public void return_(ZintBuffer param) {
				// System.out.println("packet:" + param.size());
//				size += param.size();
				if (size > 100000000) {
					TimeAxis.show("size=" + size);
					size = 0;
				}
				// byte[] temp = new byte[1000];
			}
		});

		TimeAxis.mark();

		TimeAxis.scheduleMultiThread(new Runner(tb), 1);
		TimeAxis.scheduleMultiThread(new Runner(tb), 1);
		TimeAxis.scheduleMultiThread(new Runner(tb), 1);
		TimeAxis.scheduleMultiThread(new Runner(tb), 1);
		// TimeAxis.scheduleMultiThread(new Runner(tb), 1);
		// TimeAxis.scheduleMultiThread(new Runner(tb), 1);
	}

}

class Runner implements Runnable {

	private TimeBuffer tb;

	public Runner(TimeBuffer tb) {
		this.tb = tb;
	}

	@Override
	public void run() {
		for (int i = 0; i < 200; i++) {
			ZintBuffer zb = new ZintBuffer();
			if (FastMath.random(1, 100) < 10)
				zb.writeBytes(new byte[FastMath.random(1, 2000)]);
			else
				zb.writeBytes(new byte[FastMath.random(1, 80)]);

			tb.put(zb);
		}
	}

}
