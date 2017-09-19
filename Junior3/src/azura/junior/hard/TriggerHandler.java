package azura.junior.hard;

import azura.helios6.Hnode;
import azura.helios6.write.Batch;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;
import zz.karma.JuniorEdit.K_Trigger;

public class TriggerHandler implements HardHandlerI {

	HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagTrigger;
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

		// Hnode concept = center.conceptHandler.hc.selectedItem.getNode();
		Hnode conceptCopy = center.conceptCopyHandler.hc.selectedItem.getNode();

		Batch batch = new Batch();
		// if (center.idea == null && center.ideaCopy == null) {
		// if (concept == conceptCopy && center.currentMind() ==
		// center.currentMindCopy()) {
		// center.idea = HeliosJunior3.me().addIdea(batch, center.currentMind(),
		// concept);
		// center.ideaCopy = center.idea;
		// } else {
		// center.idea = HeliosJunior3.me().addIdea(batch, center.currentMind(),
		// concept);
		// center.ideaCopy = HeliosJunior3.me().addIdea(batch,
		// center.currentMindCopy(), conceptCopy);
		// }
		// } else if (center.idea == null) {
		// center.idea = HeliosJunior3.me().addIdea(batch, center.currentMind(),
		// concept);
		// } else if (center.ideaCopy == null) {
		// center.ideaCopy = HeliosJunior3.me().addIdea(batch,
		// center.currentMindCopy(), conceptCopy);
		// }

		// boolean exist = HeliosJunior3.me().triggerExist(center.idea,
		// center.ideaCopy);
		// if (exist)
		// return;

		HardItem hiTarget = HardItem.fromNode(conceptCopy);
		HardItem hiTrigger = HardItem.fromNode(new Hnode());
		K_Trigger kt = new K_Trigger(HeliosJunior3.ksJuniorEdit);
		kt.on = 1;
		hiTrigger.data = kt.toBytes();

		hiTrigger.name = hiTarget.name;
		hiTrigger.nameTail = ".";
		if (center.roleCopyHandler.hc.selectedItem != null)
			hiTrigger.nameTail += center.roleCopyHandler.hc.selectedItem.name;
		else
			hiTrigger.nameTail += center.scriptHandler.hc.selectedItem.name;

		hc.root = center.idea;

		hc.doAdd(batch, hiTrigger);
		batch.link(hiTrigger.getNode(), center.ideaCopy);
		HeliosJunior3.me().execute(batch);

		hc.doAddRefresh();
	}

	@Override
	public void rename(String name) {
	}

	@Override
	public void delete() {
		Batch batch = new Batch();
		Hnode trigger = hc.selectedItem.getNode();
		Hnode triggerIdea = HeliosJunior3.me().triggerToIdea(trigger);
		batch.delink(center.idea, trigger).delink(HeliosJunior3.me().tagTrigger, trigger).delink(trigger, triggerIdea)
				.delete(trigger);
		HeliosJunior3.me().execute(batch);

		// if (center.idea != triggerIdea) {
		// HeliosJunior3.me().tryDeleteIdea(triggerIdea);
		// }
		// center.idea = HeliosJunior3.me().tryDeleteIdea(center.idea);

		hc.setRoot(center.idea);
		center.ideaCopy = null;
		center.conceptCopyHandler.hc.unselectHS();
	}

	@Override
	public void save(byte[] newData) {
		hc.doSave(newData);
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
