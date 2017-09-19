package azura.helios6;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;

public class Helios6<E extends Enum<E>> extends Helios6Core {
	private EnumMap<E, Hnode> enum_Node;
	private ExecutorService writerThread = Executors.newSingleThreadExecutor();

	// ============ cache ============
	private LoadingCache<Long, Hnode> cache;

	/**
	 * @param path
	 *            db file location
	 * @param clazz
	 *            tag enum
	 * @param cacheSize
	 */
	public Helios6(String path, String key, Class<E> clazz) {
		super(path, key);

		enum_Node = new EnumMap<>(clazz);
		E[] values = clazz.getEnumConstants();
		for (E e : values) {
			enum_Node.put(e, null);
		}

		writeTag();
		cleanUpHook();
	}

	public Hnode getTagNode(E tag) {
		return enum_Node.get(tag);
	}

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

	public TreeN getTree(Hnode tag, Hnode root) {
		TreeN result = new TreeN();
		TreeN current = result;
		current.cargo = root;
		LinkedList<TreeN> toSearch = new LinkedList<>();
		while (true) {
			JoinList layer = join().addFrom(current.cargo).addFrom(tag).run();
			for (Hnode child : layer) {
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

	// ================ init ==============

	private void initCache() {
		cache = CacheBuilder.newBuilder().weakValues().removalListener(new RemovalListener<Long, Hnode>() {
			@Override
			public void onRemoval(RemovalNotification<Long, Hnode> notification) {
				if (debug)
					log.debug("node removed from cache: [" + notification.toString() + "]");
			}
		}).build(new CacheLoader<Long, Hnode>() {
			@Override
			public Hnode load(Long id) throws Exception {
				return loadNode(id);
			}
		});
	}

	private void writeTag() {

		if (enum_Node == null)
			return;

		for (Entry<E, Hnode> e : enum_Node.entrySet()) {
			if (e.getValue() == null) {
				e.setValue(new Hnode(e.getKey()));
			}
			writeNode(e.getValue());
		}

		db.commit();
	}

	private void cleanUpHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			long version = db.getCurrentVersion();
			shutdown();
			long size = new File(path).length();
			log.debug("db version = " + version + ", size = " + size / 1000 + "kb");
		}));
	}

	// ================= override =================

	@Override
	protected void startUp() {
		super.startUp();
		initCache();
		writeTag();
	}

	protected Hnode getNode(Long id) {
		Hnode node = null;
		try {
			node = cache.get(id);
			if (debug)
				log.debug("get node: " + node.toString());
		} catch (ExecutionException e) {
			log.error("cache failure for id=" + id);
		}
		return node;
	}

	protected void writeNode(Hnode node) {
		super.writeNode(node);
		cache.put(node.id, node);
		if (debug)
			log.debug("write node: " + node.toString());
	}

	@Override
	protected void delete(Hnode target) {
		super.delete(target);
		cache.invalidate(target.id);
	}

	public synchronized void wipe() {
		super.wipe();
		initCache();
		writeTag();
	};

	@Override
	protected void shutdown() {
		super.shutdown();
		cache.invalidateAll();
		cache = null;
	}
	// =================== io ===============

	public synchronized byte[] saveDb() {
		byte[] bak = toBytes();
		log.info("db saved: size = " + bak.length / 1000 + "kb");
		return bak;
	}

	public synchronized void loadDb(byte[] bak) {
		wipe();
		initCache();
		writeTag();
		fromBytes(bak);
		log.info("db loaded: size = " + bak.length / 1000 + "kb");
	}

}
