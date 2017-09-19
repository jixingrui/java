package azura.helios6;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import azura.helios6.read.Condition;
import azura.helios6.read.Join;
import azura.helios6.read.JoinList;
import azura.helios6.read.Roller;
import azura.helios6.write.Op;
import common.collections.ArrayListAuto;

public class Helios6Core {
	protected static final Logger log = Logger.getLogger(Helios6Core.class);

	MVStore db;
	private MVMap<Integer, Integer> idxBank;
	private MVMap<Long, byte[]> id_node;
	private MVMap<Long[], Boolean> from_to;
	private MVMap<Long[], Boolean> to_from;

	// ============ cache ============
	private LoadingCache<Long, Hnode> cache;

	private final String path;
	private final String key;

	// ===========================
	protected boolean debug = false;

	protected Helios6Core(String path, String key) {
		this.path = path;
		this.key = key;
		boot();
		// if(debug)
		// reportState();
	}

	public void reportState() {
		int nodeCount = id_node.size();
		log.debug("\nnode count = " + nodeCount);

		StringBuilder sb = new StringBuilder();
		for (Long id : id_node.keyList()) {
			sb.append(",").append(id);
		}
		for (Long[] ft : from_to.keyList()) {
			sb.append("\n(").append(ft[0]).append(",").append(ft[1]).append(")");
		}
		log.debug(sb.toString());
	}

	void boot() {
		new File(path).getParentFile().mkdirs();
		db = new MVStore.Builder().fileName(path).autoCommitDisabled().autoCompactFillRate(75)
				.encryptionKey(key.toCharArray()).open();
		idxBank = db.openMap(NamesE.idxBank.name());
		id_node = db.openMap(NamesE.id_node.name());
		from_to = db.openMap(NamesE.from_to.name());
		to_from = db.openMap(NamesE.to_from.name());

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

	protected void shutdown() {
		cache.invalidateAll();
		cache = null;
		db.compactMoveChunks();
		db.close();
	}

	private final Hnode loadNode(Long id) {
		byte[] data = id_node.get(id);
		if (data == null) {
			log.debug("get node failed: id=" + id);
			return null;
		} else {
			Hnode node = new Hnode(id);
			node.setData(data);
			if (debug)
				log.debug("load node: " + node.toString());
			return node;
		}
	}

	public final Hnode getNode(Long id) {
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

	// ============= id management ===============

	private Long generateId() {
		Integer idx = 1;
		Integer old = idxBank.higherKey(0);
		if (old != null) {
			idx = old;
			idxBank.remove(idx);
		} else {
			idx = idxBank.get(0);
			if (idx == null)
				idx = 1;
			idxBank.put(0, idx + 1);
		}
		if (debug)
			log.debug("idx gen: " + idx);
		// Integer rand = FastMath.uidInt();
		// Long idL = idxToLong(idx, rand);
		Long idL = (long) idx;
		return idL;
	}

	private void recycleId(long id) {
		// int idx = longToIdx(id);
		int idx = (int) id;
		if (idx <= 0)
			throw new Error();
		idxBank.put(idx, idx);
		if (debug)
			log.debug("id recycled: " + idx);
		if (id == 0)
			throw new Error();
	}

	public Long idxToLong(int idx, int rand) {
		return (long) idx << 32 | (rand & 0xFFFFFFFFL);
	}

	public int longToIdx(Long L) {
		return (int) (L >>> 32);
	}

	// ============================= read =============================

	public final Join join() {
		return new Join(this);
	}

	public final synchronized JoinList join(Join join) {
		JoinList result = new JoinList();

		if (join.start < 0 || join.limit < 0)
			return result;

		List<Condition> condList = new ArrayListAuto<>();
		for (Long from : join.froms) {
			condList.add(new Condition(from, from_to));
		}
		for (Long to : join.tos) {
			condList.add(new Condition(to, to_from));
		}

		Roller roller = new Roller(condList);
		Long id = roller.next();
		int idx = 0;
		while (idx < join.start && id != null) {
			id = roller.next();
			idx++;
		}

		if (join.limit == 0)
			join.limit = Integer.MAX_VALUE;
		else
			join.limit++;

		while (id != null && result.size() < join.limit) {
			Hnode n = getNode(id);
			result.add(n);
			if (result.size() < join.limit)
				result.idxOfLastRecord = idx;
			id = roller.next();
			idx++;
		}
		if (result.size() == join.limit) {
			result.remove(result.size() - 1);
			result.hitEnd = false;
		} else {
			result.hitEnd = true;
		}
		return result;
	}

	protected final boolean isLinked(Hnode from, Hnode to) {
		if (from.id == 0 || to.id == 0) {
			log.warn("checking empty node");
			return false;
		}
		Long[] tupleKey = new Long[] { from.id, to.id };
		return from_to.containsKey(tupleKey);
	}

	private Collection<Long> getAllFroms(Hnode node) {
		List<Long> result = new ArrayList<Long>();

		List<Condition> condList = new ArrayListAuto<>();
		condList.add(new Condition(node.id, to_from));

		Roller roller = new Roller(condList);
		Long id = roller.next();
		while (id != null) {
			result.add(id);
			id = roller.next();
		}
		return result;
	}

	private Collection<Long> getAllTos(Hnode node) {
		List<Long> result = new ArrayList<Long>();

		List<Condition> condList = new ArrayListAuto<>();
		condList.add(new Condition(node.id, from_to));

		Roller roller = new Roller(condList);
		Long id = roller.next();
		while (id != null) {
			result.add(id);
			id = roller.next();
		}
		return result;
	}

	public int getLinkCount(Hnode node) {
		int count = getAllFroms(node).size();
		count += getAllTos(node).size();
		return count;
	}

	// ============================= write =============================

	void writeNode(Hnode node) {
		id_node.put(node.id, node.getData());
		cache.put(node.id, node);
		if (debug)
			log.debug("write node: " + node.toString());
	}

	// protected void wipe() {
	// idBank.clear();
	// id_node.clear();
	// from_to.clear();
	// to_from.clear();
	// db.setStoreVersion(0);
	// db.commit();
	// cache.invalidateAll();
	// db.compactRewriteFully();
	// if (debug)
	// log.debug("wipe db");
	// }

	// =============== batch ===============

	/**
	 * @link if the link is already there, fine, nothing changed
	 * @delink if the link is not here, fine, nothing changed
	 * @delete the node must be orphan otherwise the batch will rollback
	 */
	public final synchronized Batch execute(Batch batch) {
		if (batch.sealed == true) {
			throw new Error("Batch is sealed");
		}
		batch.sealed = true;

		for (int i = 0; i < batch.nodeList.size(); i++) {
			Hnode batchNode = batch.nodeList.get(i);
			if (batchNode.id == 0) {
				batchNode.id = generateId();
			} else if (batchNode.id < 0) {
				// do nothing
			} else {
				Hnode storeNode = getNode(batchNode.id);
				if (storeNode != batchNode) {
					log.error("batch node is different from cache");
					return fail(batch);
				}
			}
		}

		// op
		for (Op op : batch.opList) {
			switch (op.type) {
			case link: {
				Hnode from = batch.nodeList.get(op.one);
				Hnode to = batch.nodeList.get(op.to);
				link(from.id, to.id);
			}
				break;
			case delink: {
				Hnode from = batch.nodeList.get(op.one);
				Hnode to = batch.nodeList.get(op.to);
				delink(from.id, to.id);
			}
				break;
			case save: {
				// do nothing here
			}
				break;
			case swap: {
				Hnode from = batch.nodeList.get(op.one);
				Hnode to = batch.nodeList.get(op.to);
				swap(from, to);
			}
				break;
			case delete: {
				Hnode target = batch.nodeList.get(op.one);
				boolean success = delete(target);
				if (success == false)
					return fail(batch);
			}
				break;
			}
		}

		// save anyway
		for (int i = 0; i < batch.nodeList.size(); i++) {
			if (batch.writeNodes.get(i)) {
				Hnode node = batch.nodeList.get(i);
				writeNode(node);
			}
		}

		return success(batch);
	}

	// ====================== batch op =================
	private void link(Long from, Long to) {
		Long[] ft = new Long[] { from, to };
		Long[] tf = new Long[] { to, from };
		from_to.put(ft, true);
		to_from.put(tf, true);
		if (debug)
			log.debug("link (" + from + "," + to + ")");
	}

	private void delink(Long from, Long to) {
		Long[] ft = new Long[] { from, to };
		Long[] tf = new Long[] { to, from };
		from_to.remove(ft);
		to_from.remove(tf);
		if (debug)
			log.debug("delink (" + from + "," + to + ")");
	}

	private boolean delete(Hnode target) {
		Join jf = new Join(0, 1).addTo(target);
		Join jt = new Join(0, 1).addFrom(target);
		JoinList froms = join(jf);
		JoinList tos = join(jt);
		if (froms.size() == 0 && tos.size() == 0) {
			cache.invalidate(target.id);
			id_node.remove(target.id);
			recycleId(target.id);
			if (debug)
				log.debug("deleted node: " + target.toString());
			return true;
		} else {
			log.error("delete failed: " + target + " is not orphan");
			return false;
		}
	}

	private void swap(Hnode one, Hnode two) {
		Long swap = one.id;
		one.id = two.id;
		two.id = swap;

		Collection<Long> toOne = getAllFroms(one);
		Collection<Long> fromOne = getAllTos(one);
		Collection<Long> toTwo = getAllFroms(two);
		Collection<Long> fromTwo = getAllTos(two);

		// delink
		for (Long id : toOne) {
			delink(id, one.id);
		}
		for (Long id : fromOne) {
			delink(one.id, id);
		}
		for (Long id : toTwo) {
			delink(id, two.id);
		}
		for (Long id : fromTwo) {
			delink(two.id, id);
		}

		// link
		for (Long id : toOne) {
			link(id, two.id);
		}
		for (Long id : fromOne) {
			link(two.id, id);
		}
		for (Long id : toTwo) {
			link(id, one.id);
		}
		for (Long id : fromTwo) {
			link(one.id, id);
		}
	}

	// ================== support ============

	private Batch success(Batch batch) {
		// showVersion("success before commit");
		db.commit();
		// showVersion("success after commit");
		batch.success();
		if (debug)
			log.debug("batch success");
		return batch;
	}

	private Batch fail(Batch batch) {
		// showVersion("fail before rollback");
		db.rollback();
		// showVersion("fail after rollback");
		batch.fail();
		log.error("Batch failed, reverted.");
		return batch;
	}

	public void showVersion(String tag) {
		log.debug("========== " + tag + " ========");
		log.debug("idBank version=" + idxBank.getVersion());
		log.debug("id_node version=" + id_node.getVersion());
		log.debug("from_to version=" + from_to.getVersion());
		log.debug("to_from version=" + to_from.getVersion());
		log.debug("db version=" + db.getCurrentVersion());
		// log.debug("--------------------------------------------");
	}
}
