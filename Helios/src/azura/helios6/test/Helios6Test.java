package azura.helios6.test;

import azura.helios6.Helios6;
import azura.helios6.Hnode;
import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;
import common.algorithm.FastMath;
import common.collections.timer.TimeAxis;

public class Helios6Test extends Helios6<TestE> {

	public Hnode tag1;
	public Hnode tag2;
	public Hnode tag3;

	public Helios6Test() {
		super("./db/test.db", "test", TestE.class);
		initTag();
	}

	private void initTag() {
		tag1 = getTagNode(TestE.TAG1);
		tag2 = getTagNode(TestE.TAG2);
		tag3 = getTagNode(TestE.TAG3);
	}

	public void testJoin() {

		System.out.println("create");
		TimeAxis.mark();
		// long start = System.currentTimeMillis();

		Batch batch = new Batch();
		for (int i = 0; i < 100000; i++) {
			Hnode n = new Hnode();
			if (FastMath.randomBoolean())
				batch.link(tag1, n);
			if (FastMath.randomBoolean())
				batch.link(tag2, n);
			if (FastMath.randomBoolean())
				batch.link(tag3, n);
		}

		super.execute(batch);
		TimeAxis.show("create");

		System.out.println("join start");
		JoinList jl = join().addFrom(tag1).addFrom(tag2).run();
		// long time = System.currentTimeMillis() - start;
		System.out.println("result size=" + jl.size());

		TimeAxis.show("join");
	}

}
