package azura.karma.hard;

import java.util.List;

import org.apache.log4j.Logger;

import azura.helios6.Helios6;
import azura.helios6.Hnode;
import azura.helios6.read.Join;
import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;
import common.collections.ArrayListAuto;
import common.collections.SortedLinkedList;

public class HardCode {
	protected static Logger log = Logger.getLogger(HardCode.class);

	private static final int gap = 4;

	private final HardS_CS sc;
	private final HardHandlerI user;
	private final Helios6<?> db;

	// ================= node ===========
	public Hnode root;

	// ================== state ===============
	@SuppressWarnings("unused")
	private int pageSize;
	volatile int selectedIdx = -1;
	volatile boolean up_down;
	@SuppressWarnings("unused")
	private int indexOfLastRecord = -1;
	private boolean reachEnd = false;
	List<HardItem> upList = new ArrayListAuto<>();
	private SortedLinkedList<HardItem> downList = new SortedLinkedList<>();

	// ================= cache ==========
	public HardItem selectedItem;
	public HardItem selectedMomItem;
	public Hnode selectedMomNode;

	public HardItem heldItem;
	protected HardItem heldMomItem;

	public HardCode(HardS_CS sc, Helios6<?> db, HardHandlerI user) {
		this.sc = sc;
		this.db = db;
		this.user = user;
	}

	// ================ by karma =============

	public void addSH() {
		user.add();
	}

	public void selectSH(boolean up_down, int idx) {
		selectedMomItem = null;

		this.selectedIdx = idx;
		this.up_down = up_down;
		if (up_down) {
			selectedItem = upList.get(selectedIdx);
			if (selectedIdx - 1 >= 0) {
				selectedMomItem = upList.get(selectedIdx - 1);
			}
		} else {
			selectedItem = downList.get(selectedIdx);
			if (upList.size() >= 0) {
				selectedMomItem = upList.get(upList.size() - 1);
			}
		}
		if (selectedMomItem != null) {
			selectedMomNode = selectedMomItem.getNode();
		} else {
			selectedMomNode = root;
		}
		user.notifySelect();
	}

	void unselectSH() {
		doUnselect();
		clearDown();
		appendDown();
	}

	public void renameSH(String newName) {
		user.rename(newName);
	}

	public void saveSH(byte[] data) {
		user.save(data);
	}

	public void deleteSH() {
		user.delete();
	}

	public void holdSH() {
		heldItem = selectedItem;
		heldMomItem = selectedMomItem;
		// heldMomNode = selectedMomNode;
	}

	public void dropSH() {
		if (heldItem == null)
			return;

		user.drop();

		heldItem = null;
		heldMomItem = null;
		// heldMomNode = null;

		sc.clearHoldHS();
	}

	public void jumpSH() {
		if (user.isTree() == false)
			return;

		if (up_down) {
			upList = upList.subList(0, selectedIdx);
			selectedIdx = -1;
		} else {
			upList.add(selectedItem);
		}

		sc.refillUpHS();
		unselectSH();
		// clearDown();
		// appendDown();
	}

	public void askMoreSH(int pageSize) {
		this.pageSize = pageSize;
		// log.debug("askMore " + pageSize);
		appendDown();
	}

	// =============== hs =============
	public void unselectHS() {
		doUnselect();
		sc.unselectHS();
	}

	/**
	 * @param root
	 *            can be null
	 */
	public void setRoot(Hnode root) {
		this.root = root;
		reloadAll();
	}

	public void doAdd(Batch batch, HardItem hi) {

		if (root == null)
			throw new Error();

		if (downList.size() == 0) {
			hi.sortValue = 0;
		} else if (selectedItem == null) {
			hi.sortValue = downList.peekLast().sortValue + gap;
		} else {
			insertBefore(batch, hi);
		}

		Hnode newNode = hi.getNode();

		Hnode parentNode = null;
		HardItem parent = upList.get(upList.size() - 1);
		if (upList.size() > 0) {
			parent.numChildren++;
			parentNode = parent.getNode();
			batch.save(parentNode);
		} else {
			parentNode = root;
		}

		batch.link(user.getTagNode(), newNode).link(parentNode, newNode).save(newNode);

		// if (parentNode != root)
		// batch.save(parentNode);

		// return batch;
	}

	public void doAddRefresh() {
		HardItem momItem = getMidMomItem();
		refreshContent(momItem);
		if (momItem != null) {
			refreshMom(momItem);
		} else {
			user.notifyRefreshRelatedAll();
		}

		// self only
		clearDown();
		appendDown();
	}

	/**
	 * @param name
	 *            to change to
	 * @return success
	 */
	public boolean doRename(String name) {
		// log.info("rename " + name);
		
		if(selectedItem==null)
			return false;

		unHold();

		Hnode p = root;
		if (upList.size() > 0)
			p = upList.get(upList.size() - 1).getNode();
		boolean nameCollide = isNameCollide(p, name);
		if (nameCollide)
			return false;

		selectedItem.name = name;
		Batch b = new Batch().save(selectedItem.getNode());
		db.execute(b);

		refreshContent(selectedItem);
		return true;
	}

	private boolean isNameCollide(Hnode p, String name) {
		JoinList result = db.join().addFrom(user.getTagNode()).addFrom(p).run();
		for (Hnode n : result) {
			HardItem hi = HardItem.fromNode(n);
			if (hi.name.equals(name)) {
				log.info("warning: duplicate name");
				return true;
			}
		}
		return false;
	}

	public void doSave(byte[] newData) {
		selectedItem.data = newData;
		Batch b = new Batch().save(selectedItem.getNode());
		db.execute(b);

		refreshContent(selectedItem);
	}

	public Batch doDeleteBefore() {
		if (up_down == true)
			return null;

		heldItem = null;
		heldMomItem = null;

		Hnode target = selectedItem.getNode();

		Batch batch = new Batch().delink(user.getTagNode(), target).delink(selectedMomNode, target).delete(target);

		return batch;
	}

	public void doDeleteAfter(Batch batch) {

		db.execute(batch);
		if (batch.isSuccess() == false)
			return;

		if (selectedMomItem != null) {
			batch = new Batch();
			selectedMomItem.numChildren--;
			batch.save(selectedMomItem.getNode());
			db.execute(batch);
		}

		indexOfLastRecord--;
		downList.remove(selectedIdx);

		refreshContent(selectedMomItem);
		HardItem mm = getMidMomItem();
		if (mm != null) {
			refreshMom(mm);
			refreshChild(selectedItem);
		} else {
			user.notifyRefreshRelatedAll();
		}

		clearDown();
		appendDown();
	}

	public boolean doMoveItem() {

		if (heldItem == null) {
			log.error("Error: drop when nothing held");
			return false;
		}

		if (upList.contains(heldItem))
			return false;

		if (heldMomItem == getMidMomItem()) {
			jumpInsert();
		} else {
			toChild();
		}

		heldItem = null;
		heldMomItem = null;
		// heldMomNode = null;
		return true;
	}

	private void jumpInsert() {
		if (selectedMomItem != heldMomItem)
			return;

		if (selectedItem == null)
			return;

		Batch batch = new Batch();
		insertBefore(batch, heldItem);
		db.execute(batch);

		// refresh
		HardItem mm = getMidMomItem();
		if (mm != null) {
			refreshMom(mm);
		} else {
			user.notifyRefreshRelatedAll();
		}

		// self
		clearDown();
		appendDown();
	}

	@SuppressWarnings("unused")
	private void swapId() {
		if (selectedMomItem != heldMomItem)
			return;

		if (selectedItem == null)
			return;

		HardItem held = heldItem;
		Batch batch = new Batch().swap(selectedItem.getNode(), heldItem.getNode());

		// log.info("before swap: " + selectedItem + " " + held);

		db.execute(batch);

		// ============================ warning ================
		selectedItem.fromBytes(selectedItem.getNode().getData());
		held.fromBytes(held.getNode().getData());

		// log.info("after swap: " + selectedItem + " " + held);

		// refresh
		HardItem mm = getMidMomItem();
		if (mm != null) {
			refreshMom(mm);
		} else {
			user.notifyRefreshRelatedAll();
		}

		// self
		clearDown();
		appendDown();
	}

	private void toChild() {

		if (upList.contains(heldItem))
			return;

		HardItem newParentItem = upList.get(upList.size() - 1);
		HardItem oldParentItem = heldMomItem;

		Hnode newParent = null;
		Hnode oldParent = null;

		if (newParentItem == null)
			newParent = root;
		else
			newParent = newParentItem.getNode();

		if (oldParentItem == null)
			oldParent = root;
		else
			oldParent = oldParentItem.getNode();

		// check name
		boolean nameCollide = isNameCollide(newParent, heldItem.name);
		if (nameCollide)
			return;

		// make modification
		if (newParentItem != null) {
			newParentItem.numChildren++;
			newParent = newParentItem.getNode();
		}

		if (oldParentItem != null) {
			oldParentItem.numChildren--;
			oldParent = oldParentItem.getNode();
		}

		if (newParentItem != null)
			heldItem.sortValue = newParentItem.numChildren * gap;
		else {
			int size = db.join().addFrom(user.getTagNode()).addFrom(root).run().size();
			heldItem.sortValue = size * gap;
		}

		Hnode child = heldItem.getNode();

		Batch batch = new Batch().delink(oldParent, child).link(newParent, child);

		if (newParent != root)
			batch.save(newParent);

		if (oldParent != root)
			batch.save(oldParent);

		db.execute(batch);

		// refresh
		refreshContent(newParentItem);
		refreshContent(oldParentItem);

		HardItem mm = getMidMomItem();
		if (mm != null) {
			refreshChild(heldItem);
			refreshMom(newParentItem);
			refreshMom(oldParentItem);
			user.notifyRefreshRelatedRoot();
		} else {
			user.notifyRefreshRelatedAll();
		}

		// self
		clearDown();
		appendDown();
	}

	// ================ ability ============

	private void insertBefore(Batch batch, HardItem object) {
		if (selectedItem == downList.peekFirst()) {
			object.sortValue = selectedItem.sortValue - gap;
			batch.save(object.getNode());
		} else {
			HardItem before = downList.get(selectedIdx - 1);
			HardItem after = selectedItem;

			if (after.sortValue - before.sortValue <= 1) {
				log.info("sort value conflict: reoder all");
				downList.add(selectedIdx, object);
				for (int i = 0; i < downList.size(); i++) {
					HardItem current = downList.get(i);
					current.sortValue = i * gap;
					batch.save(current.getNode());
				}
			} else {
				object.sortValue = (before.sortValue + after.sortValue) / 2;
				batch.save(object.getNode());
			}
		}
	}

	public void reloadAll() {
		upList.clear();
		sc.refillUpHS();
		clearDown();
		appendDown();
	}

	private void doUnselect() {
		if (selectedItem != null)
			user.notifyUnselect();
		selectedIdx = -1;
		selectedItem = null;
		up_down = false;
	}

	public void clearDown() {
		doUnselect();
		indexOfLastRecord = -1;
		reachEnd = false;
		downList.clear();
		sc.clearDownHS();
	}

	public void appendDown() {
		if (reachEnd) {
			// log.debug("hard: no more down");
			return;
		}

		if (root == null) {
			// log.info("hard: no root");
			return;
		}

		Hnode p = root;
		if (upList.size() > 0)
			p = upList.get(upList.size() - 1).getNode();

		Join join = new Join().addFrom(user.getTagNode()).addFrom(p);
		JoinList result = db.join(join);

		reachEnd = result.hitEnd;
		int startIdx = downList.size();
		if (result.idxOfLastRecord >= 0)
			indexOfLastRecord = result.idxOfLastRecord;
		for (Hnode n : result) {
			HardItem hi = HardItem.fromNode(n);
			downList.addSort(hi);
		}
		sc.appendDownHS(downList.subList(startIdx, downList.size()), reachEnd);
	}

	// ================ support =============
	// public HardItem getItemParent() {
	// if (up_down)
	// return upList.get(upList.size() - 2);
	// else
	// return upList.get(upList.size() - 1);
	// }

	public Hnode getMidMomNode() {
		if (upList.size() == 0)
			return root;
		else
			return upList.get(upList.size() - 1).getNode();
	}

	public HardItem getMidMomItem() {
		if (upList.size() == 0)
			return null;
		else
			return upList.get(upList.size() - 1);
	}

	private void unHold() {
		heldItem = null;
		heldMomItem = null;
		sc.clearHoldHS();
	}

	public HardCode getHC(Enum<?> hardE) {
		return sc.hub.get(hardE).coder;
	}

	// ========================== refresh ======================
	public void refreshContent(HardItem update) {
		if (update == null)
			return;
		for (HardS_CS hsc : sc.hub.hardList) {
			hsc.coder.refreshContent1(update);
		}
	}

	private void refreshContent1(HardItem update) {
		Loc match = getLocation(update.getNodeId());
		if (match.found == false)
			return;

		match.item.fromBytes(update.toBytes());
		sc.updateOneHS(match.up_down, match.idx, match.item);
	}

	private Loc getLocation(Long id) {
		Loc loc = new Loc();
		int i;
		for (i = 0; i < upList.size(); i++) {
			loc.item = upList.get(i);
			if (loc.item.getNodeId().equals(id)) {
				loc.found = true;
				loc.up_down = true;
				break;
			}
		}
		if (loc.found == false) {
			for (i = 0; i < downList.size(); i++) {
				loc.item = downList.get(i);
				if (loc.item.getNodeId().equals(id)) {
					loc.found = true;
					loc.up_down = false;
					break;
				}
			}
		}
		loc.idx = i;
		return loc;
	}

	private static class Loc {
		boolean found;
		boolean up_down;
		int idx;
		HardItem item;
	}

	// refresh down
	public void refreshMom(HardItem in) {
		if (in == null)
			return;
		Hnode node = in.getNode();
		for (HardS_CS hsc : sc.hub.hardList) {
			if (hsc.coder == this)
				continue;
			hsc.coder.refreshMom1(node);
		}
	}

	private void refreshMom1(Hnode inNode) {
		Hnode myMom = getMidMomNode();
		if (myMom == null)
			return;
		if (myMom == inNode) {
			clearDown();
			appendDown();
		}
	}

	private void refreshChild(HardItem child) {
		if (child == null)
			return;

		Hnode childNode = child.getNode();

		for (HardS_CS hsc : sc.hub.hardList) {
			if (hsc.coder == this)
				continue;
			hsc.coder.refreshChild1(childNode);
		}
	}

	private void refreshChild1(Hnode child) {
		Loc loc = getLocation(child.getId());
		if (loc.found == false || loc.up_down == false)
			return;

		upList = upList.subList(0, loc.idx);
		sc.refillUpHS();
		clearDown();
		appendDown();
	}

	public void reloadRoot() {
		if (upList.isEmpty()) {
			clearDown();
			appendDown();
		}
	}
}
