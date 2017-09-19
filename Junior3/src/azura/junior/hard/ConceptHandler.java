package azura.junior.hard;

import azura.helios6.Hnode;
import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.junior.db.JuniorHardE;
import azura.junior.reader.IoType;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;
import common.algorithm.FastMath;
import zz.karma.JuniorEdit.K_Concept;
import zz.karma.JuniorEdit.K_Idea;

public class ConceptHandler implements HardHandlerI {

	public HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagConcept;
	}

	@Override
	public void setHardCode(HardCode hc) {
		this.hc = hc;
	}

	@Override
	public boolean isTree() {
		return true;
	}

	@Override
	public void add() {

		HardItem hi = HardItem.fromNode(new Hnode());
		hi.name = "concept" + FastMath.random(1, 99);

		K_Concept concept = new K_Concept(HeliosJunior3.ksJuniorEdit);

		concept.ioType = IoType.INTERNAL.ordinal();
//		concept.flashy = false;
		concept.counterTrigger = 0;
		concept.note = "";

		hi.data = concept.toBytes();

		Batch batch = new Batch();
		hc.doAdd(batch, hi);

		HeliosJunior3.me().execute(batch);

		HeliosJunior3.me().fillIdea(hc.root, hi.getNode());

		hc.doAddRefresh();
	}

	@Override
	public void rename(String name) {
		boolean success = hc.doRename(name);
		if (success == false)
			return;

		Hnode concept = hc.selectedItem.getNode();
		JoinList ideaList = HeliosJunior3.me().conceptToIdeaList(concept);

		Batch batch = new Batch();
		for (Hnode idea : ideaList) {
			JoinList causeList = HeliosJunior3.me().ideaToCauseList(idea);
			for (Hnode cause : causeList) {
				HardItem hiCause = HardItem.fromNode(cause);
				hiCause.name = name;
				batch.save(hiCause.getNode());
				hc.refreshContent(hiCause);
			}
			JoinList triggerList = HeliosJunior3.me().ideaToTriggerList(idea);
			for (Hnode trigger : triggerList) {
				HardItem hiTrigger = HardItem.fromNode(trigger);
				hiTrigger.name = name;
				batch.save(hiTrigger.getNode());
				hc.refreshContent(hiTrigger);
			}
		}
		HeliosJunior3.me().execute(batch);
	}

	@Override
	public void delete() {
//		boolean naked = HeliosJunior3.me().isConceptNaked(hc.selectedItem.getNode());
//		if (naked == false)
//			return;

		Batch batch = hc.doDeleteBefore();
		HeliosJunior3.me().clearIdea(batch, hc.selectedItem.getNode());
		hc.doDeleteAfter(batch);
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
		Hnode concept = hc.selectedItem.getNode();
		center.idea = HeliosJunior3.me().joinIdea(center.currentMind(), concept);
		if (center.idea == null)
			throw new Error();
		center.causeHandler.hc.setRoot(center.idea);
		center.triggerHandler.hc.setRoot(center.idea);
	}

	@Override
	public void notifyUnselect() {
		center.idea = null;
		center.causeHandler.hc.setRoot(center.idea);
		center.triggerHandler.hc.setRoot(center.idea);
	}

	@Override
	public void notifyRefreshRelatedAll() {
		hc.getHC(JuniorHardE.conceptCopy).reloadAll();
	}

	@Override
	public void notifyRefreshRelatedRoot() {
		hc.getHC(JuniorHardE.conceptCopy).reloadRoot();
	}

	public void saveIdea(K_Idea idea) {
		Hnode ideaNode = center.idea;
		ideaNode.setData(idea.toBytes());
		Batch batch = new Batch().save(ideaNode);
		HeliosJunior3.me().execute(batch);
	}

}
