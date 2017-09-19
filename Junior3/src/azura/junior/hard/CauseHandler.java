package azura.junior.hard;

import azura.helios6.Hnode;
import azura.helios6.write.Batch;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;

public class CauseHandler implements HardHandlerI {

	HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagCause;
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
		if (center.conceptCopyHandler.hc.selectedItem == null)
			return;

		Hnode concept = center.conceptHandler.hc.selectedItem.getNode();
		Hnode conceptCopy = center.conceptCopyHandler.hc.selectedItem.getNode();

		if (concept == conceptCopy && center.currentMind() == center.currentMindCopy())
			return;

		Batch batch = new Batch();
		// if (center.idea == null) {
		// center.idea = HeliosJunior3.me().addIdea(batch,
		// center.currentMind(), concept);
		// }
		// if (center.ideaCopy == null) {
		// center.ideaCopy = HeliosJunior3.me().addIdea(batch,
		// center.currentMindCopy(), conceptCopy);
		// }

		boolean exist = HeliosJunior3.me().CauseExist(center.idea, center.ideaCopy);
		if (exist)
			return;

		HardItem hiTarget = HardItem.fromNode(conceptCopy);
		HardItem hiCause = HardItem.fromNode(new Hnode());

		hiCause.name = hiTarget.name;
		hiCause.nameTail = ".";
		if (center.roleCopyHandler.hc.selectedItem != null)
			hiCause.nameTail += center.roleCopyHandler.hc.selectedItem.name;
		else
			hiCause.nameTail += center.scriptHandler.hc.selectedItem.name;

		hc.root = center.idea;

		hc.doAdd(batch, hiCause);
		batch.link(hiCause.getNode(), center.ideaCopy);
		HeliosJunior3.me().execute(batch);

		hc.doAddRefresh();
	}

	@Override
	public void rename(String name) {
	}

	@Override
	public void delete() {
		Batch batch = new Batch();
		Hnode bridge = hc.selectedItem.getNode();
		Hnode by = HeliosJunior3.me().causeToIdea(bridge);
		batch.delink(center.idea, bridge).delink(HeliosJunior3.me().tagCause, bridge).delink(bridge, by).delete(bridge);
		HeliosJunior3.me().execute(batch);

		// center.idea = HeliosJunior3.me().tryDeleteIdea(center.idea);
		// HeliosJunior3.me().tryDeleteIdea(by);

		hc.setRoot(center.idea);
		center.ideaCopy = null;
		center.conceptCopyHandler.hc.unselectHS();
	}

	@Override
	public void save(byte[] newData) {
	}

	@Override
	public void notifySelect() {
	}

	@Override
	public void notifyUnselect() {
	}

	@Override
	public void drop() {
	}

	@Override
	public void notifyRefreshRelatedAll() {
	}

	@Override
	public void notifyRefreshRelatedRoot() {
	}

}
