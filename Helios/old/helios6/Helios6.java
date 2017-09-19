package azura.helios6;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import azura.helios6.read.JoinList;

import common.util.FileUtil;

public class Helios6<E extends Enum<E>> extends Helios6Core {
	private EnumMap<E, Hnode> enum_Node;
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
	public Helios6(String path, String key, Class<E> clazz) {
		super(path, key);
		this.path = path;

		// init tags
		enum_Node = new EnumMap<>(clazz);
		E[] values = clazz.getEnumConstants();

		for (int i = 0; i < values.length; i++) {
			Hnode node = new Hnode(values[i]);
			enum_Node.put(values[i], node);
		}
		writeTag(enum_Node.values());

		cleanUpHook();
	}

	private void cleanUpHook() {
		Runtime.getRuntime().addShutdownHook(
				new Thread(() -> {
					db.compactMoveChunks();
					long version = db.getCurrentVersion();
					db.close();

					long size = new File(path).length();
					log.debug("db version = " + version + ", size = " + size
							/ 1000 + "kb");
				}));
	}

	private void writeTag(Collection<Hnode> tags) {
		if (tagLocked) {
			log.error("tags already exist");
			return;
		}

		for (Hnode tag : tags) {
			super.writeNode(tag);
		}
		tagLocked = true;
		db.commit();
	}

	public Hnode getTagNode(E tag) {
		return enum_Node.get(tag);
	}

	public byte[] save() {
		super.shutdown();
		byte[] bak = FileUtil.read(path);
		super.boot();
		log.info("db saved: version = " + getDbVersion() + ", size = "
				+ bak.length / 1000 + "kb");
		return bak;
	}

	public void load(byte[] bak) {
		super.shutdown();
		new File(path).delete();
		FileUtil.write(path, bak);
		super.boot();
		log.info("db loaded: version = " + getDbVersion() + ", size = "
				+ bak.length / 1000 + "kb");
	}

	public void wipe() {
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

	public int getDbVersion() {
		return (int) db.getCurrentVersion();
	}

	public List<Hnode> getTreeList(Hnode tag, Hnode root) {
		List<Hnode> result = new ArrayList<Hnode>();
		LinkedList<Hnode> toSearch = new LinkedList<Hnode>();
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
