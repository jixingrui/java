package azura.junior.hard;

import azura.helios6.Hnode;
import azura.helios6.read.JoinList;
import azura.helios6.write.Batch;
import azura.junior.HardCenter;
import azura.junior.db.HeliosJunior3;
import azura.junior.db.JuniorHardE;
import azura.junior.engine.JuniorSpace;
import azura.junior.engine.def.JuniorDef;
import azura.junior.engine.runtime.Actor;
import azura.junior.engine.runtime.Ego;
import azura.junior.engine.runtime.Scene;
import azura.junior.server.TestRunOutput;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;
import common.algorithm.FastMath;

public class ScriptHandler implements HardHandlerI {

	HardCode hc;
	public HardCenter center;

	@Override
	public Hnode getTagNode() {
		return HeliosJunior3.me().tagScript;
	}

	@Override
	public void setHardCode(HardCode hc) {
		this.hc = hc;
		hc.setRoot(HeliosJunior3.me().tagScriptRoot);
	}

	@Override
	public boolean isTree() {
		return true;
	}

	@Override
	public void add() {
		Batch batch = new Batch();

		Hnode newScript = new Hnode();
		HardItem hiScript = HardItem.fromNode(newScript);
		hiScript.name = "script" + FastMath.random(1, 99);
		hc.doAdd(batch, hiScript);

		Hnode soul = HeliosJunior3.me().addSoul(batch, newScript);
		HeliosJunior3.me().addMind(batch, newScript, soul);

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
		Hnode selected = hc.selectedItem.getNode();

		Batch batch = hc.doDeleteBefore();
		HeliosJunior3.me().deleteMind(batch, selected);
		HeliosJunior3.me().deleteSoul(batch, selected);
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
		Hnode script = hc.selectedItem.getNode();
		center.script = script;

		hc.getHC(JuniorHardE.role).setRoot(script);
		hc.getHC(JuniorHardE.roleCopy).setRoot(script);

		center.scriptMind = HeliosJunior3.me().sOrRToMind(script);
		center.scriptSoul = HeliosJunior3.me().mindToSoul(center.scriptMind);

		hc.getHC(JuniorHardE.concept).setRoot(center.scriptSoul);
		hc.getHC(JuniorHardE.conceptCopy).setRoot(center.scriptSoul);
	}

	@Override
	public void notifyUnselect() {
		center.script = null;
		hc.getHC(JuniorHardE.role).setRoot(null);
		hc.getHC(JuniorHardE.roleCopy).setRoot(null);
	}

	@Override
	public void notifyRefreshRelatedAll() {
	}

	@Override
	public void notifyRefreshRelatedRoot() {
	}

	public String testRun() {
		JuniorDef def = HeliosJunior3.me().genEngine();
		JuniorSpace js = new JuniorSpace(def);

		TestRunOutput output = new TestRunOutput();

		Hnode scriptNode = hc.selectedItem.getNode();

		int idScript = scriptNode.getIdAsInt();
		Scene scene = js.newScene(idScript, output, 0);
		scene.testMode = true;
		// scene.checkBoot();

		int roleIdx = 0;
		HardItem selectedRole = center.roleHandler.hc.selectedItem;
		Hnode selectedRoleNode = null;
		if (selectedRole != null)
			selectedRoleNode = selectedRole.getNode();

		JoinList jl = HeliosJunior3.me().scriptToRoleList(scriptNode);
		for (int i = 0; i < jl.size(); i++) {
			Hnode roleNode = jl.get(i);
			Hnode proNode = HeliosJunior3.me().roleToProfession(roleNode);
			Ego ego = js.newEgo(proNode.getIdAsInt(), output, 0);
			js.play(scene.ego.id, i + 1, ego.id);

			if (selectedRoleNode == roleNode) {
				roleIdx = i + 1;
			}
		}

		Hnode concept = center.conceptHandler.hc.selectedItem.getNode();
		Actor actor = scene.actorList[roleIdx];
		if (actor.mind.concept_Idea.get(concept.getIdAsInt()).defaultValue == false)
			actor.ego.turnOnInputTest(concept.getIdAsInt());

		js.dispose();

		StringBuilder log = new StringBuilder();
		log.append("======== output =======\r\n");
		log.append(output.output);
		log.append("======== outLink =======\r\n");
		log.append(output.outLink);
		log.append("========= log ========\r\n");
		log.append(output.sb);
		log.append("========= end ========\r\n");
		return log.toString();
	}

}
