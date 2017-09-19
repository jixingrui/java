package azura.helios6.test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import common.algorithm.FastMath;
import azura.helios6.Batch;
import azura.helios6.Hnode;
import azura.helios6.read.Join;
import azura.helios6.read.JoinList;

public class TestMain {

	public static void main(String[] args) {

		PropertyConfigurator.configure("./assets/log4j.properties");
		Logger.getLogger("io.netty").setLevel(Level.WARN);
		Logger.getLogger("org.apache.http").setLevel(Level.WARN);

		Logger log = Logger.getLogger(TestMain.class);

		Helios6Test db = new Helios6Test();

		Hnode node1 = new Hnode();

		ReaderA item = new ReaderA();
		item.number = 20;
		item.note = "hello";

		node1.setData(item.toBytes());

		Batch batch = new Batch().save(node1).link(db.tag1, node1);
		db.execute(batch);

		Join join = new Join().addFrom(db.tag1);
		JoinList list = db.join(join);

		for (Hnode result : list) {
			item.fromBytes(result.getData());
			log.debug("found " + result.toString() + " " + item.toString());
		}

		int deleteIdx = FastMath.random(0, list.size());
		Hnode toDelete = list.get(deleteIdx);
		batch = new Batch().delink(db.tag1, toDelete).delete(toDelete);
		db.execute(batch);

	}

}
