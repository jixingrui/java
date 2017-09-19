package azura.maze4.handler;

import azura.helios6.Hnode;
import azura.helios6.write.Batch;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;
import azura.maze4.Maze4Edit;
import azura.maze4.db.HeliosMaze;
import common.algorithm.FastMath;
import common.logger.Trace;
import zz.karma.Maze.K_Woo;

public class WooHandler implements HardHandlerI {

	private HardCode hc;

	@Override
	public Hnode getTagNode() {
		return HeliosMaze.me().tagWoo;
	}

	@Override
	public void setHardCode(HardCode hc) {
		this.hc = hc;
	}

	@Override
	public boolean isTree() {
		return false;
	}

	@Override
	public void add() {
		K_Woo rd = new K_Woo(Maze4Edit.ksMaze);
		rd.tid = FastMath.tidInt();

		Batch batch = new Batch();
		Hnode newWoo = new Hnode();

		HardItem hiWoo = HardItem.fromNode(newWoo);
		hiWoo.name = "woo" + FastMath.random(1, 99);
		rd.name = hiWoo.name;
		hiWoo.data = rd.toBytes();

		hc.doAdd(batch, hiWoo);
		HeliosMaze.me().execute(batch);
		hc.doAddRefresh();
	}

	@Override
	public void rename(String name) {
		boolean success = hc.doRename(name);
		if (success == false)
			return;

		HardItem hiWoo = hc.selectedItem;
		K_Woo kr = new K_Woo(Maze4Edit.ksMaze);
		kr.fromBytes(hiWoo.data);
		kr.name = name;
		hiWoo.data = kr.toBytes();

		Batch b = new Batch().save(hiWoo.getNode());
		HeliosMaze.me().execute(b);
	}

	@Override
	public void delete() {
		Batch batch = hc.doDeleteBefore();
		hc.doDeleteAfter(batch);
	}

	@Override
	public void save(byte[] newData) {
		hc.doSave(newData);
	}

	@Override
	public void drop() {
		if (hc.heldItem == null)
			return;

		Hnode selectedN = hc.selectedItem.getNode();
		Hnode heldN = hc.heldItem.getNode();

		Hnode selectedTo = HeliosMaze.me().wooToWoo(selectedN);

		if (hc.selectedItem == hc.heldItem) {
			Trace.trace("clear door link");
			// drop to self: clear link;
			if (selectedTo != null) {
				doorClear(hc.selectedItem);
				selectedN = hc.selectedItem.getNode();
				Batch batch = new Batch().save(hc.selectedItem.getNode()).delink(selectedN, selectedTo);
				HeliosMaze.me().execute(batch);
				hc.clearDown();
				hc.appendDown();
			}

		} else {
			Trace.trace("link door");
			// link two doors
			Hnode heldItemTo = HeliosMaze.me().wooToWoo(heldN);

			doorLink(hc.selectedItem, hc.heldItem);

			selectedN = hc.selectedItem.getNode();
			heldN = hc.heldItem.getNode();

			if (HeliosMaze.me().isLinked(selectedN, heldN) && HeliosMaze.me().isLinked(heldN, selectedN))
				return;

			Batch batch = new Batch().save(selectedN).save(heldN);

			if (selectedTo != null)
				batch.delink(selectedN, selectedTo);
			batch.link(selectedN, heldN);

			if (!selectedN.equals(heldN)) {
				if (heldItemTo != null)
					batch.delink(heldN, heldItemTo);
				batch.link(heldN, selectedN);
			}
			HeliosMaze.me().execute(batch);
			hc.clearDown();
			hc.appendDown();
		}
	}

	private void doorLink(HardItem leftN, HardItem rightN) {
		K_Woo left = new K_Woo(HeliosMaze.me().ksMaze);
		left.fromBytes(leftN.data);
		left.doorToName = rightN.name;

		K_Woo right = new K_Woo(HeliosMaze.me().ksMaze);
		right.fromBytes(rightN.data);
		right.doorToName = leftN.name;
		
		left.doorToTid=right.tid;
		right.doorToTid=left.tid;

		leftN.data = left.toBytes();
		rightN.data = right.toBytes();
	}

	private void doorClear(HardItem door) {
		K_Woo dr = new K_Woo(HeliosMaze.me().ksMaze);
		dr.fromBytes(door.data);
		dr.doorToName = "nowhere";
		door.data = dr.toBytes();
	}

	@Override
	public void notifySelect() {
	}

	@Override
	public void notifyUnselect() {
	}

	@Override
	public void notifyRefreshRelatedAll() {
	}

	@Override
	public void notifyRefreshRelatedRoot() {
	}

}
