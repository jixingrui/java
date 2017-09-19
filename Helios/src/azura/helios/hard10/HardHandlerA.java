package azura.helios.hard10;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

import azura.helios.hard10.ie.HardCSE;
import azura.helios.hard10.ie.HardSCE;
import azura.helios.hard10.ie.HardServerI;
import azura.helios5.HeliosNode;
import azura.helios5.batch.Batch;
import azura.helios5.join.Join;
import azura.helios5.join.JoinList;

import common.collections.ArrayListAuto;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.ZintReaderI;

public abstract class HardHandlerA implements HardServerI {
	protected static Logger log = Logger.getLogger(HardHandlerA.class);

	// ================== setting ====================
	private final HeliosNode tagNode;
	// private final BitSet permission = new BitSet();
	private boolean isTree;
	int id = -1;
	protected HardHub<?> hub;
	public HeliosNode root;
	private int pageSize;

	// ================== state ===============
	private volatile int selectedIdx = -1;
	private volatile boolean up_down;
	private int indexOfLastRecord = -1;
	private boolean hasNext = true;
	private List<HardItem> upList = new ArrayListAuto<>();
	private List<HardItem> downList = new ArrayListAuto<>();

	// ================== hold ================
	protected volatile HardItem heldItem;
	/**
	 * can be null, which means the parent node is root
	 */
	protected volatile HardItem heldItemParent;

	public HardHandlerA(boolean isTree, HeliosNode tagNode) {
		this.isTree = isTree;
		this.tagNode = tagNode;
	}

	// ================= support ==================

	public HardItem getItemParent() {
		if (up_down)
			return upList.get(upList.size() - 2);
		else
			return upList.get(upList.size() - 1);
	}

	public HardItem getSelectedItem() {
		if (up_down)
			return upList.get(selectedIdx);
		else
			return downList.get(selectedIdx);
	}

	public HardItem getHeldItem() {
		return heldItem;
	}

	public String getSelectedPath() {

		if (getSelectedItem() == null)
			return "";

		int fromUpIdx;
		String result = "";

		if (up_down == false) {
			result = result + getSelectedItem().name + ".";
			fromUpIdx = upList.size() - 1;
		} else {
			fromUpIdx = selectedIdx;
		}

		for (int i = fromUpIdx; i >= 0; i--) {
			result = result + upList.get(i).name + ".";
		}
		return result + ".";
	}

	public void update() {
		updateSc(up_down, selectedIdx, getSelectedItem());
	}

	public void select(long id) {
		for (int i = 0; i < downList.size(); i++) {
			HardItem current = downList.get(i);
			if (current.getNode(false).id == id) {
				select_(false, i);
			}
		}
	}

	private void select_(boolean up_down, int idx) {
		selectedIdx = idx;
		this.up_down = up_down;
		select();
	}

	public void selectSc(long id) {
		select(id);
		selectSc(false, selectedIdx);
	}

	// ================== template =================

	/*
	 * data is from client
	 */
	public abstract void add(byte[] data);

	public abstract void rename(String name);

	public abstract void save(byte[] data);

	public abstract void delete();

	public abstract void select();

	public abstract void unselect();

	/**
	 * Tree handler occupy drop action when 'drop to unselected place'
	 */
	public abstract void drop();

	// ================== drop ===============
	public void swapId() {
		if (getItemParent() != heldItemParent)
			return;

		HardItem selected = getSelectedItem();
		if (selected == null)
			return;

		HardItem held = heldItem;
		// int heldIdx = downList.indexOf(held);
		Batch batch = new Batch().swap(selected.getNode(false),
				heldItem.getNode(false)).seal();

		log.info("before swap: " + selected + " " + held);

		hub.helios.executeAsync(batch).thenRun(() -> {

			selected.fromBytes(selected.getNode(false).data);
			held.fromBytes(held.getNode(false).data);

			log.info("after swap: " + selected + " " + held);

			clearDown();
			searchDown(false);
		});
	}

	private void toChild() {

		if (upList.contains(heldItem))
			return;

		HardItem newParentItem = upList.get(upList.size() - 1);
		HardItem oldParentItem = heldItemParent;

		HeliosNode newParent = null;
		HeliosNode oldParent = null;
		HeliosNode child = heldItem.getNode(false);

		if (newParentItem == null)
			newParent = root;
		else
			newParent = newParentItem.getNode(false);

		if (oldParentItem == null)
			oldParent = root;
		else
			oldParent = oldParentItem.getNode(false);

		// make modification
		if (newParentItem != null) {
			newParentItem.numChildren++;
			newParent = newParentItem.getNode(true);
		}

		if (oldParentItem != null) {
			oldParentItem.numChildren--;
			oldParent = oldParentItem.getNode(true);
		}

		Batch batch = new Batch().delink(oldParent, child).link(newParent,
				child);

		if (newParent != root)
			batch.save(newParent);

		if (oldParent != root)
			batch.save(oldParent);

		batch.seal();

		hub.helios.executeNow(batch);

		if (newParentItem != null)
			updateSc(true, upList.size() - 1, newParentItem);

		clearDown();
		searchDown(false);
	}

	// ================= cs ===================
	@Override
	public void hold() {
		heldItem = getSelectedItem();
		if (heldItem != null)
			heldItemParent = getItemParent();
		else
			heldItemParent = null;
	}

	@Override
	public void drop_() {
		HardItem selected = getSelectedItem();
		HardItem selectedParent = getItemParent();

		if (heldItem == null) {
			log.error("Error: drop when nothing held");
			return;
		}

		if (selected == null && isTree && selectedParent != heldItemParent)
			toChild();

		drop();

		heldItem = null;
		heldItemParent = null;
	}

	public HeliosNode doAdd(String name, byte[] data) {
		HardItem hi = new HardItem();

		HeliosNode newNode = hub.helios.newNode();
		hi.fromNode(newNode);
		hi.name = name;
		hi.dataPure = data;
		newNode = hi.getNode(true);

		HeliosNode parentNode = null;
		HardItem parent = upList.get(upList.size() - 1);
		if (upList.size() > 0) {
			parent.numChildren++;
			parentNode = parent.getNode(true);
		} else {
			parentNode = root;
		}

		Batch batch = new Batch().link(tagNode, newNode)
				.link(parentNode, newNode).save(newNode);

		if (parentNode != root)
			batch.save(parentNode);

		batch.seal();

		hub.helios.executeNow(batch);
		if (parent != null)
			updateSc(true, upList.size() - 1, parent);
		searchDown(true);
		return newNode;
	}

	/**
	 * @param name
	 *            to change to
	 * @return false if there is duplicate in sibling
	 */
	public boolean doRename(String name) {
		log.info("rename " + name);

		heldItem = null;
		heldItemParent = null;

		HeliosNode p = root;
		if (upList.size() > 0)
			p = upList.get(upList.size() - 1).getNode(false);
		Join join = new Join().addFrom(tagNode).addFrom(p);
		JoinList result = hub.helios.join(join);
		for (HeliosNode n : result) {
			HardItem hi = new HardItem().fromNode(n);
			if (hi.name.equals(name)) {
				return false;
			}
		}

		HardItem selected = getSelectedItem();
		selected.name = name;
		Batch b = new Batch().save(selected.getNode(true)).seal();
		hub.helios.executeNow(b);

		renameSC(name);
		return true;
	}

	public void doDelete() {
		heldItem = null;
		heldItemParent = null;

		HeliosNode target = getSelectedItem().getNode(false);

		// prepareDelete(target);
		hub.helios.deleteByForce(target);
		deleteNotifyParent();
	}

	private boolean targetHasChild() {
		HeliosNode target = getSelectedItem().getNode(false);
		Join j = new Join(0, 1).addFrom(target).addFrom(tagNode);
		return !hub.helios.join(j).isEmpty();
	}

	private void deleteNotifyParent() {
		// HeliosNode target = getSelectedItem().getNode(false);
		// log.info("delete " + target.id);
		HardItem parentItem = getItemParent();
		HeliosNode parent;

		if (parentItem != null) {
			parentItem.numChildren--;
			parent = parentItem.getNode(true);
		} else {
			parent = root;
		}

		Batch batch = new Batch();

		if (parentItem != null)
			batch.save(parent);

		batch.seal();

		CompletableFuture<Boolean> txn = hub.helios
				.executeNowCommitFuture(batch);

		if (txn == null)
			return;

		txn.thenRun(() -> {
			indexOfLastRecord--;
			downList.remove(selectedIdx);
			deleteSc(false, selectedIdx);
			unselect_();

			if (parentItem != null)
				updateSc(true, upList.size() - 1, parentItem);
		});

		txn.complete(true);
	}

	public void doSave(byte[] data) {
		log.info("save");

		HardItem selected = getSelectedItem();

		selected.dataPure = data;

		Batch batch = new Batch().save(selected.getNode(true)).seal();

		hub.helios.executeNow(batch);

		updateSc(up_down, selectedIdx, selected);
	}

	@Override
	public void jump() {
		log.info("jump");

		if (!isTree)
			return;

		if (up_down) {
			upList = upList.subList(0, selectedIdx);
			selectedIdx = -1;
		} else {
			upList.add(getSelectedItem());
		}

		// unselect();
		fillUpSc(upList);
		clearDown();
		searchDown(true);
	}

	public void clearDown() {
		unselect_();
		indexOfLastRecord = -1;
		hasNext = true;
		downList.clear();
		clearSc(false);
	}

	@Override
	public void unselect_() {
		log.info("unselect");
		selectedIdx = -1;
		unselect();
	}

	@Override
	public void askMore(boolean up_down, int pageSize) {
		log.info("ask more isUp=" + up_down + " pageSize=" + pageSize);
		this.pageSize = pageSize;
		searchDown(false);
	}

	public void searchDown(boolean force) {
		if (!force && !hasNext) {
			log.debug("hard: no more down");
			return;
		}

		if (root == null) {
			log.info("hard not initialized");
			return;
		}

		HeliosNode p = root;
		if (upList.size() > 0)
			p = upList.get(upList.size() - 1).getNode(false);

		Join join = new Join(indexOfLastRecord + 1, pageSize).addFrom(tagNode)
				.addFrom(p);
		JoinList result = hub.helios.join(join);

		hasNext = result.hasNext;
		int startIdx = downList.size();
		if (result.idxOfLastRecord >= 0)
			indexOfLastRecord = result.idxOfLastRecord;
		for (HeliosNode n : result) {
			HardItem hi = new HardItem();
			hi.fromNode(n);
			downList.add(hi);
		}
		appendDownSc(downList.subList(startIdx, downList.size()), hasNext);
	}

	// ========================= encode ======================
	private void renameSC(String name) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(HardSCE.rename.ordinal());
		zb.writeUTFZ(name);
		hub.send(id, zb.toBytes());
	}

	private void updateSc(boolean up_down, int idx, HardItem item) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(HardSCE.update.ordinal());
		zb.writeBoolean(up_down);
		zb.writeZint(idx);
		zb.writeBytesZ(item.toBytes());
		hub.send(id, zb.toBytes());
	}

	private void fillUpSc(List<HardItem> list) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(HardSCE.fillUp.ordinal());
		zb.writeZint(list.size());
		for (int i = 0; i < list.size(); i++) {
			HardItem hi = list.get(i);
			zb.writeBytesZ(hi.toBytes());
		}
		hub.send(id, zb.toBytes());
	}

	private void appendDownSc(List<HardItem> list, boolean hasNext) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(HardSCE.appendDown.ordinal());
		zb.writeBoolean(hasNext);
		zb.writeZint(list.size());
		for (int i = 0; i < list.size(); i++) {
			HardItem hi = list.get(i);
			zb.writeBytesZ(hi.toBytes());
		}
		byte[] data = zb.toBytes();
		hub.send(id, data);
	}

	private void selectSc(boolean up_down, int idx) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(HardSCE.select.ordinal());
		zb.writeBoolean(up_down);
		zb.writeZint(idx);
		hub.send(id, zb.toBytes());
	}

	private void deleteSc(boolean up_down, int idx) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(HardSCE.delete.ordinal());
		zb.writeBoolean(up_down);
		zb.writeZint(idx);
		hub.send(id, zb.toBytes());
	}

	private void clearSc(boolean up_down) {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(HardSCE.clear.ordinal());
		zb.writeBoolean(up_down);
		hub.send(id, zb.toBytes());
	}

	// ========================= decode ======================
	public void receive(byte[] msg) {
		ZintReaderI zb = new ZintBuffer(msg);
		HardCSE op;
		try {
			op = HardCSE.values()[zb.readZint()];
		} catch (Exception e1) {
			log.error("msg type: decoding failed");
			return;
		}
		switch (op) {
		case delete_m:
			if (selectedIdx > -1) {
				if (targetHasChild()) {
					log.debug("cannot delete a parent");
					return;
				}
				delete();
			}
			break;
		case rename_m:
			if (selectedIdx > -1) {
				String name;
				try {
					name = zb.readUTFZ();
				} catch (Exception e) {
					log.error("rename: decoding failed");
					return;
				}
				rename(name);
			}
			break;
		case add_m:
			// if (permission.get(HardCSE.add_m.ordinal()))
			add(zb.readBytesZ());
			break;
		case save_m:
			if (selectedIdx > -1) {
				byte[] data;
				try {
					data = zb.readBytesZ();
				} catch (Exception e) {
					log.error("save: decoding failed");
					return;
				}
				save(data);
			}
			break;
		case select: {
			boolean up_down;
			int idx;
			try {
				up_down = zb.readBoolean();
				idx = zb.readZint();
			} catch (Exception e) {
				log.error("select: decoding failed");
				return;
			}
			select_(up_down, idx);
			// log.info("select " + getSelectedItem().getNode(false));
		}
			break;
		case askMore: {
			boolean up_down;
			int pageSize;
			try {
				up_down = zb.readBoolean();
				pageSize = zb.readZint();
			} catch (Exception e) {
				log.error("askMore: decoding failed");
				return;
			}
			askMore(up_down, pageSize);
		}
			break;
		case hold:
			hold();
			break;
		case drop:
			drop_();
			break;
		case unselect:
			unselect_();
			break;
		case jump:
			jump();
			break;
		}
	}

}
