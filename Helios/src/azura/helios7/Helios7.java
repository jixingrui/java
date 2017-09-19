package azura.helios7;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import azura.helios7.read.JoinList;

public class Helios7<E extends Enum<E>> extends Helios7Core {
	private EnumMap<E, Hnode7> enum_Node;
	private volatile boolean tagLocked;
	private ExecutorService writerThread = Executors.newSingleThreadExecutor();
	private String path;

	/**
	 * @param path
	 *            db file location
	 * @param clazz
	 *            tag enum
	 * @param cacheSize
	 */
	public Helios7(String path, Class<E> clazz) {
		super(path);
		this.path = path;

		// init tags
		enum_Node = new EnumMap<>(clazz);
		E[] values = clazz.getEnumConstants();

		for (int i = 0; i < values.length; i++) {
			Hnode7 node = new Hnode7(values[i]);
			enum_Node.put(values[i], node);
		}
		writeTag(enum_Node.values());

	}

	private void writeTag(Collection<Hnode7> tags) {
		if (tagLocked) {
			log.error("tags already exist");
			return;
		}

		for (Hnode7 tag : tags) {
			super.writeNode(tag);
		}
		tagLocked = true;
		db.commit();
	}

	public Hnode7 getTagNode(E tag) {
		return enum_Node.get(tag);
	}

	public synchronized byte[] saveDb() {
		byte[] bak = super.toBytes();
		log.info("db saved: size = " + bak.length / 1000 + "kb");
		return bak;
	}

	public synchronized void loadDb(byte[] bak) {
		wipe();
		shutdown();
		boot();
		fromBytes(bak);
		log.info("db loaded: size = " + bak.length / 1000 + "kb");
	}

	public synchronized void wipe() {
		super.shutdown();
		tagLocked = false;
		new File(path).delete();
		super.boot();
		writeTag(enum_Node.values());
	};

	/**
	 * @link if the link is already there, fine, nothing changed
	 * @delink if the link is not here, fine, nothing changed
	 * @delete the node must be orphan otherwise the batch will rollback
	 */
	public final CompletableFuture<Batch> executeAsync(Batch batch) {
		return CompletableFuture.supplyAsync(() -> {
			this.execute(batch);
			return batch;
		}, writerThread);
	}

	public TreeN getTree(Hnode7 tag, Hnode7 root) {
		TreeN result = new TreeN();
		TreeN current = result;
		current.cargo = root;
		LinkedList<TreeN> toSearch = new LinkedList<>();
		while (true) {
			JoinList layer = join().addFrom(current.cargo).addFrom(tag).run();
			for (Hnode7 child : layer) {
				TreeN childT = new TreeN();
				childT.cargo = child;
				current.addChild(childT);
				toSearch.addLast(childT);
			}
			if (toSearch.isEmpty())
				break;
			current = toSearch.removeFirst();
		}
		return result;
	}

	public List<Hnode7> getTreeList(Hnode7 tag, Hnode7 root) {
		List<Hnode7> result = new ArrayList<Hnode7>();
		LinkedList<Hnode7> toSearch = new LinkedList<Hnode7>();
		while (true) {
			JoinList layer = join().addFrom(root).addFrom(tag).run();
			result.addAll(layer);
			toSearch.addAll(layer);
			if (toSearch.isEmpty())
				break;
			root = toSearch.removeFirst();
		}
		return result;
	}
}

// public synchronized byte[] saveDbOld() {
// // db.compactRewriteFully();
// // db.compact(arg0, arg1);
// super.shutdown();
// byte[] bak = FileUtil.read(path);
// super.boot();
// log.info("db saved: version = " + getDbVersion() + ", size = " + bak.length /
// 1000 + "kb");
// // showVersion("save db");
// return bak;
// }
//
// public synchronized void loadDbOld(byte[] bak) {
// super.shutdown();
// new File(path).delete();
// FileUtil.write(path, bak);
// super.boot();
// log.info("db loaded: version = " + getDbVersion() + ", size = " + bak.length
// / 1000 + "kb");
// // showVersion("load db");
// }