package azura.helios6;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import azura.helios6.read.Condition;
import azura.helios6.read.Join;
import azura.helios6.read.JoinList;
import azura.helios6.read.Roller;
import azura.helios6.write.Batch;
import azura.helios6.write.Op;
import azura.helios6.write.OpE;
import common.algorithm.crypto.RC4;
import common.collections.ArrayListAuto;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import common.util.FileUtil;

public class Helios6Core implements BytesI {
	protected static final Logger log = Logger.getLogger(Helios6Core.class);

	MVStore db;
	private MVMap<Integer, Integer> idxBank;
	private MVMap<Long, byte[]> id_node;
	private MVMap<long[], Boolean> from_to;
	private MVMap<long[], Boolean> to_from;

	protected final String path;
	private final String key;

	// ===========================
	protected boolean debug = false;

	protected Helios6Core(String path, String key) {
		this.path = path;
		this.key = key;
		startUp();
		if (debug)
			reportState();
	}

	public void reportState() {
		int nodeCount = id_node.size();
		log.debug("\nnode count = " + nodeCount);

		StringBuilder sb = new StringBuilder();
		for (Long id : id_node.keyList()) {
			sb.append(",").append(id);
		}
		for (long[] ft : from_to.keyList()) {
			sb.append("\n(").append(ft[0]).append(",").append(ft[1]).append(")");
		}
		log.debug(sb.toString());
	}

	protected void startUp() {
		new File(path).getParentFile().mkdirs();
		db = new MVStore.Builder().fileName(path).autoCommitDisabled().encryptionKey(key.toCharArray()).open();
		idxBank = db.openMap(NamesE.idxBank.name());
		id_node = db.openMap(NamesE.id_node.name());
		from_to = db.openMap(NamesE.from_to.name());
		to_from = db.openMap(NamesE.to_from.name());
	}

	protected void shutdown() {
		db.compactMoveChunks();
		db.close();
	}

	protected final Hnode loadNode(Long id) {
		byte[] data = id_node.get(id);
		if (data == null) {
			log.error("node not exist: id=" + id);
			return null;
		} else {
			Hnode node = new Hnode(id);
			node.setData(data);
			if (debug)
				log.debug("load node: " + node.toString());
			return node;
		}
	}

	protected Hnode getNode(Long id) {
		throw new Error();
		// return loadNode(id);
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

	public final boolean isLinked(Hnode from, Hnode to) {
		if (from.id == 0 || to.id == 0) {
			log.warn("checking empty node");
			return false;
		}
		long[] tupleKey = new long[] { from.id, to.id };
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

	protected void writeNode(Hnode node) {
		id_node.put(node.id, node.getData());
	}

	protected void wipe() {
		idxBank.clear();
		id_node.clear();
		from_to.clear();
		to_from.clear();
		db.commit();
		db.compactMoveChunks();
		log.info("db wiped. version=" + db.getCurrentVersion());
	}

	// =============== batch ===============

	/**
	 * @link if the link is already there, fine, nothing changed
	 * @delink if the link is not here, fine, nothing changed
	 * @delete the node must be orphan otherwise the batch will rollback
	 */
	public final synchronized void execute(Batch batch) {
		if (batch.sealed == true) {
			throw new Error("Batch is sealed");
		}
		batch.sealed = true;
		if (db.hasUnsavedChanges())
			throw new Error("=================== unsaved changes !!! ===================");

		// showVersion("before batch");

		for (int i = 0; i < batch.nodeList.size(); i++) {
			Hnode batchNode = batch.nodeList.get(i);
			if (batchNode.id != 0) {
				Hnode storeNode = getNode(batchNode.id);
				if (storeNode != batchNode) {
					log.error("batch node is different from cache");
					batch.fail();
					return;
				}
			}
		}
		// ================== check ==================
		for (Op op : batch.opList) {
			if (op.type != OpE.delete)
				continue;

			// check delink from
			HashSet<Hnode> fromSet = new HashSet<>();
			JoinList froms = join().addTo(op.oneNode()).run();
			fromSet.addAll(froms);
			for (Op opF : batch.opList) {
				if (opF.type == OpE.link && opF.to == op.one) {
					fromSet.add(opF.oneNode());
				}
				if (opF.type == OpE.delink && opF.to == op.one) {
					fromSet.remove(opF.oneNode());
				}
				if (fromSet.isEmpty())
					break;
			}
			if (fromSet.size() > 0) {
				batch.fail();
				return;
			}

			// check delink to
			HashSet<Hnode> toSet = new HashSet<>();
			JoinList tos = join().addFrom(op.oneNode()).run();
			toSet.addAll(tos);
			for (Op opT : batch.opList) {
				if (opT.type == OpE.link && opT.one == op.one) {
					toSet.add(opT.toNode());
				}
				if (opT.type == OpE.delink && opT.one == op.one) {
					toSet.remove(opT.toNode());
				}
				if (toSet.isEmpty())
					break;
			}
			if (toSet.size() > 0) {
				batch.fail();
				return;
			}
		}

		// ================== start write =================

		HashSet<Hnode> newNodes = new HashSet<>();
		for (int i = 0; i < batch.nodeList.size(); i++) {
			Hnode batchNode = batch.nodeList.get(i);
			if (batchNode.id == 0) {
				batchNode.id = generateId();
				newNodes.add(batchNode);
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
			case delete: {
				Hnode target = batch.nodeList.get(op.one);
				delete(target);
			}
				break;
			case save: {
				Hnode target = batch.nodeList.get(op.one);
				writeNode(target);
				newNodes.remove(target);
			}
				break;
			}
		}
		for (Hnode newNode : newNodes) {
			writeNode(newNode);
		}

		db.commit();
		batch.success();
		return;
	}

	// ====================== batch op =================
	private void link(Long from, Long to) {
		long[] ft = new long[] { from, to };
		long[] tf = new long[] { to, from };
		from_to.put(ft, true);
		to_from.put(tf, true);
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

	protected void delete(Hnode target) {
		id_node.remove(target.id);
		recycleId(target.id);
	}

	// ================== support ============

	public void showVersion(String tag) {
		log.debug("========== " + tag + " ========");
		log.debug("idBank version=" + idxBank.getVersion());
		log.debug("id_node version=" + id_node.getVersion());
		log.debug("from_to version=" + from_to.getVersion());
		log.debug("to_from version=" + to_from.getVersion());
		log.debug("db version=" + db.getCurrentVersion());
	}
	// ==================== io ====================

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
		from_to.forEach((k, v) -> {
			zb.writeLong(k[0]);
			zb.writeLong(k[1]);
		});
		byte[] out = zb.toBytes();
		out = FileUtil.compress(out);
		out = new RC4(key.getBytes(Charset.forName("UTF8"))).rc4(out);
		return out;
	}

	@Override
	public void fromBytes(byte[] bytes) {
		bytes = new RC4(key.getBytes(Charset.forName("UTF8"))).rc4(bytes);
		bytes = FileUtil.uncompress(bytes);

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
			from_to.put(k, true);
			k = new long[] { t, f };
			to_from.put(k, true);
		}
		db.commit();
	}

	// ==================== recycle =================
	@SuppressWarnings("unused")
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
}
