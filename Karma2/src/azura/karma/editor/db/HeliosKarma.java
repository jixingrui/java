package azura.karma.editor.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.mapdb.HTreeMap;

import azura.helios.hard10.HardItem;
import azura.helios5.Helios5;
import azura.helios5.HeliosNode;
import azura.helios5.batch.Batch;
import azura.helios5.join.JoinList;
import azura.karma.def.KarmaNow;
import azura.karma.def.KarmaSpace;
import azura.karma.def.tree.TreeNode;
import azura.karma.editor.def.KarmaDef;
import azura.karma.editor.def.KarmaDefPack;
import azura.karma.editor.def.KarmaFieldPack;
import azura.karma.editor.def.KarmaTooth;
import azura.karma.editor.service.CheckSet;
import common.collections.ArrayListAuto;
import common.collections.buffer.ZintBuffer;

public class HeliosKarma extends Helios5<KarmaE> {
	static class SingletonHolder {
		static HeliosKarma instance = new HeliosKarma();
	}

	public static HeliosKarma singleton() {
		return SingletonHolder.instance;
	}

	private HTreeMap<Integer, Long> id_idn;

	public HeliosNode tagKarmaRoot = getTagNode(KarmaE.KarmaRoot);
	public HeliosNode tagKarma = getTagNode(KarmaE.Karma);
	public HeliosNode tagField = getTagNode(KarmaE.Field);
	public HeliosNode tagFork = getTagNode(KarmaE.Fork);

	// cache
	private HashMap<Integer, KarmaNow> id_node_cache = new HashMap<Integer, KarmaNow>();
	private ArrayListAuto<ForkNode> forkCache = new ArrayListAuto<ForkNode>();
	private HashMap<Long, KarmaDefPack> node_karma = new HashMap<Long, KarmaDefPack>();
	private HashMap<KarmaDefPack, Box> k_b = new HashMap<>();

	public HeliosKarma() {
		super("db/karma.helios", KarmaE.class);
		id_idn = db.createHashMap("id_idn").makeOrGet();
		db.commit();
	}

	@Override
	public void wipe() {
		id_idn.clear();
		super.wipe();
	}

	public boolean updateFork(HeliosNode karma, String name) {
		JoinList list = createJoin().addTo(karma).addFrom(tagFork).run();
		if (list.size() == 0)
			return false;

		Batch batch = new Batch();
		for (HeliosNode fork : list) {
			HardItem hi = new HardItem().fromNode(fork);
			hi.name = name;
			batch.save(hi.getNode(true));
		}
		batch.seal();
		executeNow(batch);
		return true;
	}

	public ZintBuffer packKarma(HeliosNode k) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(k.data);
		JoinList fieldList = createJoin().addFrom(k).addFrom(tagField).run();
		zb.writeZint(fieldList.size());
		for (HeliosNode field : fieldList) {
			zb.writeBytesZ(field.data);
		}
		return zb;
	}

	public void indexKarma(int id, long idn) {
		id_idn.put(id, idn);
		db.commit();
	}

	// ====================== tree ====================
	/**
	 * @return with start
	 */
	public LinkedList<KarmaDefPack> getUpListKarma(HeliosNode start) {
		LinkedList<KarmaDefPack> upList = new LinkedList<KarmaDefPack>();
		HeliosNode child = start;
		HeliosNode parent = null;

		KarmaDefPack kc = null;
		KarmaDefPack kp = null;

		while (child != null) {
			kc = nodeToKarma(child);
			upList.addFirst(kc);

			parent = getParentKarma(child);
			if (parent != null) {
				kp = nodeToKarma(parent);
				kc.core.tidParent = kp.core.tid;
			}
			child = parent;
		}
		return upList;
	}

	/**
	 * @return without start
	 */
	public LinkedList<KarmaDefPack> getDownTreeKarma(HeliosNode start) {
		LinkedList<KarmaDefPack> downTreeList = new LinkedList<KarmaDefPack>();
		LinkedList<HeliosNode> stack = new LinkedList<HeliosNode>();

		HeliosNode parent = start;

		while (parent != null) {
			KarmaDef kp = nodeToKarma(parent).core;

			JoinList childList = getChildListKarma(parent);
			for (HeliosNode child : childList) {

				KarmaDefPack kc = nodeToKarma(child);
				kc.core.tidParent = kp.tid;

				downTreeList.add(kc);
				stack.add(child);
			}
			parent = stack.pollFirst();
		}
		return downTreeList;
	}

	public LinkedHashSet<HeliosNode> getRefList(KarmaDefPack target) {
		LinkedHashSet<HeliosNode> refList = new LinkedHashSet<HeliosNode>();
		JoinList fieldList = createJoin().addFrom(tagField).addFrom(k_b.get(target).node).run();
		for (HeliosNode field : fieldList) {
			JoinList forkList = createJoin().addFrom(field).addFrom(tagFork).run();
			for (HeliosNode fork : forkList) {
				HeliosNode ref = createJoin().addFrom(fork).addFrom(tagKarma).run().get(0);
				refList.add(ref);
			}
		}
		return refList;
	}

	public KarmaDefPack nodeToKarma(HeliosNode node) {

		KarmaDefPack kde = node_karma.get(node.id);
		if (kde != null)
			return kde;

		kde = nodeToKarmaPure(node);
		node_karma.put(node.id, kde);

		// field
		JoinList fl = createJoin().addFrom(node).addFrom(tagField).run();
		for (HeliosNode fn : fl) {
			HardItem hf = new HardItem();
			hf.fromNode(fn);
			KarmaFieldPack kf = new KarmaFieldPack();
			kf.core.fromBytes(hf.dataPure);
			kf.core.name = hf.name;

			JoinList ffl = createJoin().addFrom(fn).run();
			for (HeliosNode teeth : ffl) {
				HeliosNode ref = createJoin().addFrom(teeth).run().get(0);
				KarmaDefPack temp = nodeToKarmaPure(ref);
				KarmaTooth fork = new KarmaTooth();
				fromNode(fork, teeth);
				fork.targetType = temp.core.tid;
				fork.targetName = temp.core.name;
				kf.fork.add(fork);
			}

			kde.fieldList.add(kf);
		}

		return kde;
	}

	private void fromNode(KarmaTooth fork, HeliosNode node) {
		HardItem hi = new HardItem();
		hi.fromNode(node);
		ZintBuffer reader = new ZintBuffer(hi.dataPure);
		fork.readFrom(reader);
	}

	private KarmaDefPack nodeToKarmaPure(HeliosNode node) {
		HardItem hi = new HardItem();
		hi.fromNode(node);
		KarmaDefPack kde = new KarmaDefPack();
		kde.core.fromBytes(hi.dataPure);
		// kde.node = node;
		k_b.put(kde, new Box());
		k_b.get(kde).node = node;
		return kde;
	}

	private HeliosNode getParentKarma(HeliosNode karma) {
		return createJoin().addTo(karma).addFrom(tagKarma).run().get(0);
	}

	private JoinList getChildListKarma(HeliosNode karma) {
		return createJoin().addFrom(karma).addFrom(tagKarma).run();
	}

	public KarmaSpace genSpace(HeliosNode root) {
		node_karma.clear();

		KarmaSpace space = new KarmaSpace();
		Iterable<KarmaDefPack> que = getRelatedQue(root);
		Batch batch = new Batch();

		for (KarmaDefPack k : que) {

			boolean changed = recordHistory(k);
			if (changed) {
				Box box = k_b.get(k);
				HardItem hi = new HardItem().fromNode(box.node);
				hi.dataPure = k.core.toBytes();
				box.node.data = hi.toBytes();
				batch.save(box.node);
			}

			KarmaNow kn = new KarmaNow(space);
			kn.fromEditor(k);
			space.putDef(kn.editor.core.tid, kn);

			TreeNode tn = new TreeNode();
			tn.id = kn.editor.core.tid;
			space.tree.addNode(tn);

			if (space.tree.getRoot() == tn)
				continue;

			int idParent = kn.editor.core.tidParent;
			TreeNode parent = space.tree.getNode(idParent);
			if (parent == null)
				throw new Error();

			parent.addChild(tn);
		}

		batch.seal();
		executeNow(batch);

		return space;
	}

	private boolean recordHistory(KarmaDefPack k) {
		KarmaSpace space = new KarmaSpace();
		KarmaNow now = new KarmaNow(space);
		boolean changed = now.fromEditor(k);
		if (changed) {
			now.editor.core.historydata = now.history.toBytes();
		}
		return changed;

	}

	private Iterable<KarmaDefPack> getRelatedQue(HeliosNode selected) {
		CheckSet<KarmaDefPack> que = new CheckSet<KarmaDefPack>();

		LinkedList<KarmaDefPack> up = getUpListKarma(selected);
		LinkedList<KarmaDefPack> down = getDownTreeKarma(selected);
		que.addAll(up);
		que.addAll(down);

		while (que.allChecked() == false) {
			KarmaDefPack A = que.removeFirst();
			LinkedHashSet<HeliosNode> refL = getRefList(A);
			for (HeliosNode B : refL) {
				LinkedList<KarmaDefPack> upB = getUpListKarma(B);
				que.addAll(upB);
			}
		}
		return que;
	}

	public void loadKarma(KarmaSpace space) {
		forkCache.clear();
		HashSet<KarmaNow> newNodeSet = new HashSet<KarmaNow>();

		Batch batch = new Batch();
		for (TreeNode tn : space.tree.getRoot().getBroadFirstList()) {
			KarmaNow newNode = space.getDef(tn.id);
			k_b.put(newNode.editor, new Box());
			id_node_cache.put(newNode.editor.core.tid, newNode);
			if (id_idn.containsKey(newNode.editor.core.tid)) {
				Box box = k_b.get(newNode.editor);
				long idn = id_idn.get(newNode.editor.core.tid);
				HeliosNode storeNode = getNode(idn);
				box.node = storeNode;
				HardItem hi = new HardItem();
				hi.fromNode(storeNode);

				boolean nameChanged = false;

				box.hi = hi;
				KarmaDef storeK = new KarmaDef();
				storeK.fromBytes(hi.dataPure);
				if (storeK.versionSelf == newNode.editor.core.versionSelf) {
					log.info("Karma of same version already exist " + newNode.editor.core.name);
				} else if (storeK.versionSelf > newNode.editor.core.versionSelf) {
					log.info("Warning: node cannot down grade: " + storeK.name);
				} else if (storeK.versionSelf < newNode.editor.core.versionSelf) {
					log.info("Karma updates: " + storeK.name + "." + storeK.versionSelf + " to "
							+ newNode.editor.core.name + "." + newNode.editor.core.versionSelf);
					hi.dataPure = newNode.editor.core.toBytes();
					if (hi.name.equals(newNode.editor.core.name) == false) {
						nameChanged = true;
						hi.name = newNode.editor.core.name;
					}
					storeNode.data = hi.toBytes();
					box.node = storeNode;
					batch.save(storeNode);

					// delete old field
					JoinList fieldList = createJoin().addFrom(storeNode).addFrom(tagField).run();
					for (HeliosNode fieldN : fieldList) {
						JoinList teethList = createJoin().addFrom(tagFork).addFrom(fieldN).run();
						for (HeliosNode teethN : teethList) {
							HeliosNode ref = createJoin().addFrom(teethN).run().get(0);
							batch.delink(teethN, ref).delink(tagFork, teethN).delink(fieldN, teethN).delete(teethN);
						}
						batch.delink(tagField, fieldN).delink(storeNode, fieldN).delete(fieldN);
					}

					addField(batch, newNode);
					// ============ update field ============

					// ======= update fork name ============
					if (nameChanged == false)
						continue;

					JoinList list = createJoin().addTo(storeNode).addFrom(tagFork).run();
					if (list.size() == 0)
						continue;
					for (HeliosNode fork : list) {
						HardItem hiF = new HardItem().fromNode(fork);
						hiF.name = newNode.editor.core.name;
						batch.save(hiF.getNode(true));
					}

				}
				continue;
			}

			if (newNode.isTop()) {
				addKarmaTop(batch, newNode);
			} else {
				KarmaNow parent = newNode.getParent();
				addKarma(batch, parent, newNode);
			}
			newNodeSet.add(newNode);
		}

		for (ForkNode temp : forkCache) {
			KarmaNow kn = id_node_cache.get(temp.fork.targetType);
			KarmaDefPack ref = kn.editor;
			HardItem hi = new HardItem();
			hi.fromNode(new HeliosNode());
			hi.name = ref.core.name;
			hi.dataPure = temp.fork.toBytes();
			HeliosNode teeth = hi.getNode(true);
			KarmaDefPack kde = id_node_cache.get(ref.core.tid).editor;
			Box box = k_b.get(kde);
			HeliosNode node = box.node;
			batch.link(tagFork, teeth).link(temp.fieldNode, teeth).link(teeth, node).save(teeth);
		}

		batch.seal();
		executeNow(batch);

		for (KarmaNow p : newNodeSet) {
			id_node_cache.put(p.editor.core.tid, p);
			Box box = k_b.get(p.editor);
			indexKarma(p.editor.core.tid, box.node.id);
		}
	}

	private void addKarmaTop(Batch batch, KarmaNow child) {
		Box box = k_b.get(child.editor);
		box.hi = new HardItem();
		box.hi.fromNode(new HeliosNode());
		box.hi.name = child.editor.core.name;
		box.hi.dataPure = child.editor.core.toBytes();
		box.node = box.hi.getNode(true);
		batch.link(tagKarma, box.node).link(tagKarmaRoot, box.node).save(box.node);

		addField(batch, child);
	}

	private void addKarma(Batch batch, KarmaNow parent, KarmaNow child) {
		Box boxP = k_b.get(parent.editor);
		boxP.hi.numChildren++;
		boxP.node = boxP.hi.getNode(true);

		Box boxC = k_b.get(child.editor);
		boxC.hi = new HardItem();
		boxC.hi.fromNode(newNode());
		boxC.hi.name = child.editor.core.name;
		boxC.hi.dataPure = child.editor.core.toBytes();
		boxC.node = boxC.hi.getNode(true);
		batch.link(tagKarma, boxC.node).link(boxP.node, boxC.node).save(boxC.node).save(boxP.node);

		addField(batch, child);
	}

	private void addField(Batch batch, KarmaNow kn) {
		for (KarmaFieldPack field : kn.editor.fieldList) {
			HardItem hi = new HardItem();
			hi.fromNode(new HeliosNode());
			hi.dataPure = field.core.toBytes();
			hi.name = field.core.name;

			HeliosNode fieldN = hi.getNode(true);
			Box box = k_b.get(kn.editor);
			batch.link(box.node, fieldN).link(tagField, fieldN).save(fieldN);

			if (field.fork.size() > 0) {
				for (KarmaTooth teeth : field.fork) {
					ForkNode forkN = new ForkNode();
					forkN.fork = teeth;
					forkN.fieldNode = fieldN;
					forkCache.add(forkN);
				}
			}
		}
	}
}
