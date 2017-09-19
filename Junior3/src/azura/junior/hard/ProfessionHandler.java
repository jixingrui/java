package azura.junior.hard;

import azura.helios6.Hnode;
import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;
import common.algorithm.FastMath;

public class ProfessionHandler implements HardHandlerI {

	public HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagProfession;
	}

	@Override
	public void setHardCode(HardCode hc) {
		this.hc = hc;
		hc.setRoot(HeliosJunior3.me().tagProfessionRoot);
	}

	@Override
	public boolean isTree() {
		return true;
	}

	@Override
	public void add() {
		Hnode newIdentity = new Hnode();
		HardItem hiIdentity = HardItem.fromNode(newIdentity);
		hiIdentity.name = "identity" + FastMath.random(1, 99);
		Batch batch = new Batch();
		hc.doAdd(batch, hiIdentity);
		HeliosJunior3.me().addSoul(batch, newIdentity);
		HeliosJunior3.me().execute(batch);
		hc.doAddRefresh();
	}

	@Override
	public void rename(String name) {
		name = name.replace(".", "_");
		hc.doRename(name);

		Batch batch = new Batch();

		Hnode soul = HeliosJunior3.me().getSoul(hc.selectedItem.getNode());
		JoinList mindList = HeliosJunior3.me().soulToMindList(soul);
		for (Hnode mind : mindList) {
			Hnode role = HeliosJunior3.me().mindToRole(mind);
			HardItem hiRole = HardItem.fromNode(role);
			hiRole.nameTail = "." + name;
			batch.save(hiRole.getNode());

			hc.refreshContent(hiRole);
		}

		HeliosJunior3.me().execute(batch);
	}

	@Override
	public void delete() {
		Batch batch = hc.doDeleteBefore();
		HeliosJunior3.me().deleteSoul(batch, hc.selectedItem.getNode());
		hc.doDeleteAfter(batch);
	}

	@Override
	public void save(byte[] newData) {
	}

	@Override
	public void drop() {
		hc.doMoveItem();
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
