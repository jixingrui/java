package azura.helios5;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.mapdb.Atomic;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import org.mapdb.HTreeMap;

import azura.helios5.batch.Batch;
import azura.helios5.batch.Op;
import azura.helios5.join.Condition;
import azura.helios5.join.Join;
import azura.helios5.join.JoinList;
import azura.helios5.join.Roller;

import common.algorithm.FastMath;
import common.collections.ArrayListAuto;
import common.collections.bitset.BitSet;
import common.util.FileUtil;

public class Helios5Core {
	protected static final Logger log = Logger.getLogger(Helios5Core.class);

	protected DB db;
	private NavigableSet<Fun.Tuple2<Long, Long>> from_to;
	private NavigableSet<Fun.Tuple2<Long, Long>> to_from;
	private Atomic.Long idMax;
	private HTreeMap<Long, HeliosNode> id_node;
	private HTreeMap<String, Long> uid_id;
	private volatile boolean tagLocked;

	protected Helios5Core(String path, int cacheSize) {
		File file = FileUtil.getCreateFile(path);
		try {
			db = DBMaker.newFileDB(file).closeOnJvmShutdown()
					.cacheSize(cacheSize).make();

			from_to = db.createTreeSet(NamesE.from_to.name())
					.comparator(Fun.TUPLE2_COMPARATOR).makeOrGet();
			to_from = db.createTreeSet(NamesE.to_from.name())
					.comparator(Fun.TUPLE2_COMPARATOR).makeOrGet();
			idMax = db.getAtomicLong(NamesE.id_increaser.name());
			id_node = db.createHashMap(NamesE.id_node.name())
					.valueSerializer(new HeliosNode()).makeOrGet();
			uid_id = db.createHashMap(NamesE.uid_node.name()).makeOrGet();

			db.commit();
			db.compact();

		} catch (Exception e) {
			log.fatal("helios db creation failed", e);
		}
	}

	protected void initTag(Collection<HeliosNode> tags) {
		if (tagLocked) {
			log.error("tags can only init once");
			return;
		}

		tagLocked = true;
		for (HeliosNode tag : tags) {
			id_node.putIfAbsent(tag.id, tag);
		}
		db.commit();
	}

	// ============================= read =============================
	public final HeliosNode getNode(Long id) {
		HeliosNode result = id_node.get(id);
		if (result == null)
			return null;
		else
			return result.clone();
	}

	public final synchronized HeliosNode getNode(String uid) {
		Long id = uid_id.get(uid);
		return getNode(id);
	}

	public final Join createJoin() {
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
			HeliosNode n = getNode(id);
			result.add(n);
			if (result.size() < join.limit)
				result.idxOfLastRecord = idx;
			id = roller.next();
			idx++;
		}
		if (result.size() == join.limit) {
			result.remove(result.size() - 1);
			result.hasNext = true;
		} else {
			result.hasNext = false;
		}
		return result;
	}

	protected final boolean isLinked(HeliosNode from, HeliosNode to) {
		if (from.id == 0 || to.id == 0) {
			log.warn("checking empty node");
			return false;
		}
		return from_to.contains(Fun.t2(from.id, to.id));
	}

	// ============================= write =============================
	/**
	 * provided for HardHandlerA only. no need for uid at the moment.
	 */
	public final synchronized HeliosNode newNode() {
		HeliosNode node = new HeliosNode(idMax.incrementAndGet());
		node.timeStamp = FastMath.tidLong();
		HeliosNode stored = id_node.putIfAbsent(node.id, node);
		if (stored != null) {
			log.fatal("node creation failed: idMax is not max");
			db.rollback();
			log.fatal("increasing idMax to recover");
			idMax.set(node.id + 1);
			db.commit();
		} else {
//			log.info("new " + node);
		}
		return node.clone();
	}

	/**
	 * @link if the link is already there, fine, nothing changed
	 * @delink if the link is not here, fine, nothing changed
	 * @delete the node must be orphan otherwise the batch will rollback
	 */
	public final synchronized void executeNow(Batch batch) {
		CompletableFuture<Boolean> future = executeNowCommitFuture(batch);
		if (future != null)
			future.complete(true);
	}

	/**
	 * @return.null batch failed, nothing to return
	 * @return.future.true commit
	 * @return.future.false rollback
	 */
	public final synchronized CompletableFuture<Boolean> executeNowCommitFuture(
			Batch batch) {

		if (!isValid(batch)) {
			log.error("batch canceled");
			return null;
		}

		// batch.success=true;

		BitSet bsNewNode = new BitSet();

		// create all phantom nodes
		for (int i = 0; i < batch.nodeList.size(); i++) {
			HeliosNode node = batch.nodeList.get(i);
			if (node.id == 0) {
				bsNewNode.set(i);
				node.id = idMax.incrementAndGet();
				node.stamp();

				// if(node.id==154)
				// log.info("stop");

				// check
				HeliosNode oldId = id_node.putIfAbsent(node.id, node.clone());
				if (oldId != null) {
					log.fatal("node creation failed: idMax is not max");
					db.rollback();
					log.fatal("increasing idMax to recover");
					idMax.set(node.id + 1);
					db.commit();
					return null;
				}

				if (node.uid.length() > 0) {
					Long oldUid = uid_id.putIfAbsent(node.uid, node.id);
					if (oldUid != null) {
						log.fatal("node creation failed: uid conflict");
						db.rollback();
						return null;
					}
				}
			}
		}

		boolean success = true;
		loop: for (Op op : batch.opList) {
			switch (op.type) {
			case link: {
				HeliosNode from = batch.nodeList.get(op.from);
				HeliosNode to = batch.nodeList.get(op.to);
				link(from.id, to.id);
			}
				break;
			case delink: {
				HeliosNode from = batch.nodeList.get(op.from);
				HeliosNode to = batch.nodeList.get(op.to);
				delink(from.id, to.id);
			}
				break;
			case delete: {
				HeliosNode target = batch.nodeList.get(op.from);
				success = delete(target);
				if (!success)
					break loop;
			}
				break;
			case save: {
				// save later
			}
				break;
			case swap: {

				HeliosNode one = batch.nodeList.get(op.from);
				HeliosNode two = batch.nodeList.get(op.to);
				HeliosNode oneOld = id_node.get(one.id);
				HeliosNode twoOld = id_node.get(two.id);

				Collection<Long> toOne = getAllFroms(one);
				Collection<Long> fromOne = getAllTos(one);
				Collection<Long> toTwo = getAllFroms(two);
				Collection<Long> fromTwo = getAllTos(two);

				one.data = twoOld.data;
				two.data = oneOld.data;
				id_node.replace(one.id, one.clone());
				id_node.replace(two.id, two.clone());

				// delink
				for (long id : toOne) {
					delink(id, one.id);
				}
				for (long id : fromOne) {
					delink(one.id, id);
				}
				for (long id : toTwo) {
					delink(id, two.id);
				}
				for (long id : fromTwo) {
					delink(two.id, id);
				}

				// link
				for (long id : toOne) {
					link(id, two.id);
				}
				for (long id : fromOne) {
					link(two.id, id);
				}
				for (long id : toTwo) {
					link(id, one.id);
				}
				for (long id : fromTwo) {
					link(one.id, id);
				}

//				log.info("swap success= " + success);
			}
				break;
			}
		}

		if (success) {
			for (int i = 0; i < batch.nodeList.size(); i++) {
				HeliosNode node = batch.nodeList.get(i);

				if (node.id < 0)
					continue;

				if (bsNewNode.get(i))
					continue;

				if (batch.deleteNodes.get(i))
					continue;

				if (batch.saveNodes.get(i)) {
					success = save(node);
				} else {
					success = stamp(node);
				}
			}
		}

		if (success) {
			batch.success();
			CompletableFuture<Boolean> commitFuture = new CompletableFuture<Boolean>();
			commitFuture.thenAccept(commit -> {
				if (commit) {
					db.commit();
				} else {
					log.error("rollback ordered from Future");
					db.rollback();
				}
			});
			return commitFuture;
		} else {
			log.error("batch failed. rolling back");
			batch.fail();
			db.rollback();
			return null;
		}
	}

	private boolean save(HeliosNode target) {
		HeliosNode c = target.clone();
		c.stamp();
		target.timeStamp = c.timeStamp;

		HeliosNode replaced = id_node.replace(c.id, c);
		if (replaced != null) {
//			log.debug("saved " + c + " data=" + c.data.length);
			return true;
		} else {
//			log.error("save failed, node not exist");
			return false;
		}
	}

	private boolean stamp(HeliosNode target) {
		HeliosNode old = id_node.get(target.id);
		if (old == null)
			return false;

		old.stamp();
		target.timeStamp = old.timeStamp;
		id_node.replace(old.id, old);
		return true;
	}

	private void link(long from, long to) {
		Tuple2<Long, Long> ft = Fun.t2(from, to);
		Tuple2<Long, Long> tf = Fun.t2(to, from);
		from_to.add(ft);
		to_from.add(tf);
//		log.debug("linked (" + from + "," + to + ")");
	}

	private void delink(long from, long to) {
		Tuple2<Long, Long> ft = Fun.t2(from, to);
		Tuple2<Long, Long> tf = Fun.t2(to, from);
		from_to.remove(ft);
		to_from.remove(tf);
//		log.debug("delink (" + from + "," + to + ")");
	}

	private boolean delete(HeliosNode target) {
		Join jf = new Join(0, 1).addTo(target);
		Join jt = new Join(0, 1).addFrom(target);
		JoinList froms = join(jf);
		JoinList tos = join(jt);
		if (froms.size() == 0 && tos.size() == 0) {
			id_node.remove(target.id);
			if (target.uid.length() > 0) {
				uid_id.remove(target.uid);
			}
//			log.debug("deleted " + target);
			return true;
		} else {
			log.error("delete failed: " + target + " is not orphan");
			return false;
		}
	}

	public void deleteByForce(HeliosNode target) {
		Join jf = new Join().addTo(target);
		Join jt = new Join().addFrom(target);
		JoinList froms = join(jf);
		JoinList tos = join(jt);
		Batch batch = new Batch();
		for (HeliosNode f : froms) {
			batch.delink(f, target);
		}
		for (HeliosNode t : tos) {
			batch.delink(target, t);
		}
		batch.delete(target).seal();
		executeNow(batch);
	}

	private Collection<Long> getAllFroms(HeliosNode node) {
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

	private Collection<Long> getAllTos(HeliosNode node) {
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

	private boolean isValid(Batch batch) {

		if (!batch.isSealed()) {
			log.error("batch invalid: not sealed");
			batch.fail();
			return false;
		}

		// all referenced nodes must exist
		for (int i = 0; i < batch.nodeList.size(); i++) {
			HeliosNode newNode = batch.nodeList.get(i);
			if (newNode.id != 0) {
				HeliosNode oldNode = getNode(newNode.id);
				if (oldNode == null) {
					log.error("batch invalid: node not exist: " + newNode);
					batch.fail();
					return false;
				}

				if (!oldNode.matchTime(newNode)) {
//					log.warn("batch in danger: node outdated: " + newNode);
					// batch.fail();
					// return false;
				}
			} else if (!batch.saveNodes.get(i) && newNode.data.length > 0) {
				log.error("new node contains data");
				batch.fail();
				return false;
			}
		}

		return true;
	}

	protected void wipe() {
		from_to.clear();
		to_from.clear();
		idMax.set(0);
		id_node.clear();
		uid_id.clear();
		db.commit();
		db.compact();
		tagLocked = false;
	}
}
