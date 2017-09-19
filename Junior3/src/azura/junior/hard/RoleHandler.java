package azura.junior.hard;

import common.algorithm.FastMath;
import azura.helios6.Hnode;
import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.junior.db.JuniorHardE;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;

public class RoleHandler implements HardHandlerI {

	public HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagRole;
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
		HardItem professionItem = hc.getHC(JuniorHardE.profession).selectedItem;
		if (professionItem == null)
			return;

		Hnode professionNode = professionItem.getNode();

		Hnode newRole = new Hnode();
		HardItem hiRole = HardItem.fromNode(newRole);
		hiRole.name = FastMath.random(1, 99) + "";
		hiRole.nameTail = "." + professionItem.name;
		Batch batch = new Batch();
		hc.doAdd(batch, hiRole);
		Hnode soul = HeliosJunior3.me().getSoul(professionNode);
		HeliosJunior3.me().addMind(batch, newRole, soul);
		batch.link(professionNode, newRole);
		HeliosJunior3.me().execute(batch);
		hc.doAddRefresh();
	}

	@Override
	public void rename(String name) {
		boolean success = hc.doRename(name);
		if (success == false)
			return;

		Batch batch = new Batch();

		Hnode mind = HeliosJunior3.me().sOrRToMind(hc.selectedItem.getNode());
		JoinList ideaList = HeliosJunior3.me().mindToIdeaList(mind);
		for (Hnode idea : ideaList) {
			JoinList triggerList = HeliosJunior3.me().ideaToTriggerList(idea);
			for (Hnode trigger : triggerList) {
				HardItem hiTrigger = HardItem.fromNode(trigger);
				hiTrigger.nameTail = "." + name;
				batch.save(hiTrigger.getNode());

				hc.refreshContent(hiTrigger);
			}

			JoinList causeList = HeliosJunior3.me().ideaToCauseList(idea);
			for (Hnode cause : causeList) {
				HardItem hiCause = HardItem.fromNode(cause);
				hiCause.nameTail = "." + name;
				batch.save(hiCause.getNode());

				hc.refreshContent(hiCause);
			}
		}

		HeliosJunior3.me().execute(batch);
	}

	@Override
	public void delete() {
		Hnode role = hc.selectedItem.getNode();

		Hnode pro = HeliosJunior3.me().roleToProfession(role);

		Batch batch = hc.doDeleteBefore();
		batch.delink(pro, role);
		HeliosJunior3.me().deleteMind(batch, role);
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
		center.roleMind = HeliosJunior3.me().sOrRToMind(hc.selectedItem.getNode());
		center.roleSoul = HeliosJunior3.me().mindToSoul(center.roleMind);
		center.conceptHandler.hc.setRoot(center.roleSoul);
	}

	@Override
	public void notifyUnselect() {
		center.roleMind = null;
		center.roleSoul = null;
		center.conceptHandler.hc.setRoot(center.scriptSoul);
	}

	@Override
	public void notifyRefreshRelatedAll() {
		hc.getHC(JuniorHardE.roleCopy).reloadAll();
	}

	@Override
	public void notifyRefreshRelatedRoot() {
		hc.getHC(JuniorHardE.roleCopy).reloadRoot();
	}

}
