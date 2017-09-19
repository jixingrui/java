package azura.helios7;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.mapdb.DB;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import azura.helios7.read.Condition;
import azura.helios7.read.Join;
import azura.helios7.read.JoinList;
import azura.helios7.read.Roller;
import azura.helios7.write.Op;
import common.collections.ArrayListAuto;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class Helios7Core implements BytesI {
	protected static final Logger log = Logger.getLogger(Helios7Core.class);

	DB db;
	private NavigableMap<Integer, Integer> idxBank;
	private Map<Long, byte[]> id_node;
	private NavigableSet<long[]> from_to;
	private NavigableSet<long[]> to_from;

	// ============ cache ============
	private LoadingCache<Long, Hnode7> cache;

//	private final String path;

	// ===========================
	protected boolean debug = false;

	protected Helios7Core(String path) {
//		this.path = path;
		boot();
		// if(debug)
		// reportState();
	}

	void boot() {
//		File f=new File(path);
//		f.getParentFile().mkdirs();
//		db = DBMaker.memoryDB().transactionEnable().closeOnJvmShutdown().make();
//		idxBank = db.treeMap(NamesE.idxBank.name(), Serializer.INTEGER, Serializer.INTEGER).createOrOpen();
//		id_node = db.hashMap(NamesE.id_node.name(), Serializer.LONG, Serializer.BYTE_ARRAY).createOrOpen();
//		from_to = db.treeSet(NamesE.from_to.name(), Serializer.LONG_ARRAY).createOrOpen();
//		to_from = db.treeSet(NamesE.to_from.name(), Serializer.LONG_ARRAY).createOrOpen();

		cache = CacheBuilder.newBuilder().weakValues().removalListener(new RemovalListener<Long, Hnode7>() {
			@Override
			public void onRemoval(RemovalNotification<Long, Hnode7> notification) {
				if (debug)
					log.debug("node removed from cache: [" + notification.toString() + "]");
			}
		}).build(new CacheLoader<Long, Hnode7>() {
			@Override
			public Hnode7 load(Long id) throws Exception {
				return loadNode(id);
			}
		});
	}

	protected void shutdown() {
		cache.invalidateAll();
		cache = null;
		db.close();
	}

	private final Hnode7 loadNode(Long id) {
		byte[] data = id_node.get(id);
		if (data == null) {
			log.error("node not exist: id=" + id);
			return null;
		} else {
			Hnode7 node = new Hnode7(id);
			node.setData(data);
			if (debug)
				log.debug("load node: " + node.toString());
			return node;
		}
	}

	public final Hnode7 getNode(Long id) {
		Hnode7 node = null;
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
		Long idL = (long) idx;
		return idL;
	}

	private void recycleId(long id) {
		int idx = (int) id;
		if (idx <= 0)
			throw new Error();
		idxBank.put(idx, idx);
		if (debug)
			log.debug("id recycled: " + idx);
		if (id == 0)
			throw new Error();
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
			Hnode7 n = getNode(id);
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

	public final boolean isLinked(Hnode7 from, Hnode7 to) {
		if (from.id == 0 || to.id == 0) {
			log.warn("checking empty node");
			return false;
		}
		long[] tupleKey = new long[] { from.id, to.id };
		return from_to.contains(tupleKey);
	}

	private Collection<Long> getAllFroms(Hnode7 node) {
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

	private Collection<Long> getAllTos(Hnode7 node) {
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

	public int getLinkCount(Hnode7 node) {
		int count = getAllFroms(node).size();
		count += getAllTos(node).size();
		return count;
	}

	// ============================= write =============================

	void writeNode(Hnode7 node) {
		id_node.put(node.id, node.getData());
		cache.put(node.id, node);
		if (debug)
			log.debug("write node: " + node.toString());
	}

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
			Hnode7 batchNode = batch.nodeList.get(i);
			if (batchNode.id == 0) {
				batchNode.id = generateId();
			} else if (batchNode.id < 0) {
				// do nothing
			} else {
				Hnode7 storeNode = getNode(batchNode.id);
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
				Hnode7 from = batch.nodeList.get(op.one);
				Hnode7 to = batch.nodeList.get(op.to);
				link(from.id, to.id);
			}
				break;
			case delink: {
				Hnode7 from = batch.nodeList.get(op.one);
				Hnode7 to = batch.nodeList.get(op.to);
				delink(from.id, to.id);
			}
				break;
			case save: {
				// do nothing here
			}
				break;
			case swap: {
				Hnode7 from = batch.nodeList.get(op.one);
				Hnode7 to = batch.nodeList.get(op.to);
				swap(from, to);
			}
				break;
			case delete: {
				Hnode7 target = batch.nodeList.get(op.one);
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
				Hnode7 node = batch.nodeList.get(i);
				writeNode(node);
			}
		}

		return success(batch);
	}

	// ====================== batch op =================
	private void link(Long from, Long to) {
		long[] ft = new long[] { from, to };
		long[] tf = new long[] { to, from };
		from_to.add(ft);
		to_from.add(tf);
		if (debug)
			log.debug("link (" + from + "," + to + ")");
	}

	private void delink(Long from, Long to) {
		long[] ft = new long[] { from, to };
		long[] tf = new long[] { to, from };
		from_to.remove(ft);
		to_from.remove(tf);
		if (debug)
			log.debug("delink (" + from + "," + to + ")");
	}

	private boolean delete(Hnode7 target) {
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

	private void swap(Hnode7 one, Hnode7 two) {
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

	public void reportState() {
		int nodeCount = id_node.size();
		log.debug("\nnode count = " + nodeCount);

		StringBuilder sb = new StringBuilder();
		for (Long id : id_node.keySet()) {
			sb.append(",").append(id);
		}
		for (long[] ft : from_to) {
			sb.append("\n(").append(ft[0]).append(",").append(ft[1]).append(")");
		}
		log.debug(sb.toString());
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(idxBank.size());
		idxBank.forEach((k, v) -> {
			zb.writeZint(k);
			zb.writeZint(v);
		});
		zb.writeZint(id_node.size());
		id_node.forEach((k, v) -> {
			zb.writeLong(k);
			zb.writeBytesZ(v);
		});
		zb.writeZint(from_to.size());
		from_to.forEach((k) -> {
			zb.writeLong(k[0]);
			zb.writeLong(k[1]);
		});
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		int size;
		ZintBuffer zb = new ZintBuffer(bytes);
		size = zb.readZint();
		for (int i = 0; i < size; i++) {
			idxBank.put(zb.readZint(), zb.readZint());
		}
		size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Long k = zb.readLong();
			byte[] v = zb.readBytesZ();
			id_node.put(k, v);
		}
		size = zb.readZint();
		for (int i = 0; i < size; i++) {
			Long f = zb.readLong();
			Long t = zb.readLong();
			long[] k = new long[] { f, t };
			from_to.add(k);
			k = new long[] { t, f };
			to_from.add(k);
		}
	}
}
