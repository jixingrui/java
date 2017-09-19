package azura.helios6.test;

import azura.helios6.Helios6;
import azura.helios6.Hnode;

public class Helios6Test extends Helios6<TestE> {

	public Hnode tag1;

	public Helios6Test() {
		super("./db/test.db", "test", TestE.class);
		initTag();
	}

	private void initTag() {
		tag1 = getTagNode(TestE.TAG1);
	}

}
