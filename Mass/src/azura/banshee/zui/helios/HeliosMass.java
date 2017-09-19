package azura.banshee.zui.helios;

import java.util.HashMap;

import azura.banshee.zui.model.MassTree;
import azura.banshee.zui.model.ScreenSetting;
import azura.banshee.zui.model.v2016083001.MassAction0830;
import azura.banshee.zui.model.v2016083001.MassBox0830;
import azura.banshee.zui.model.v2016083001.MassTreeN0830;
import azura.helios.hard10.HardItem;
import azura.helios5.Helios5;
import azura.helios5.HeliosNode;
import azura.helios5.batch.Batch;
import azura.helios5.join.Join;
import azura.helios5.join.JoinList;

public class HeliosMass extends Helios5<MassE> {
	static class SingletonHolder {
		static HeliosMass instance = new HeliosMass();
	}

	public static HeliosMass singleton() {
		return SingletonHolder.instance;
	}

	public HeliosNode tagRoot = getTagNode(MassE.root);
	public HeliosNode tagBox = getTagNode(MassE.box);
	public HeliosNode tagAction = getTagNode(MassE.action);
	public HeliosNode tagTarget = getTagNode(MassE.target);
	public HeliosNode ssNode = getTagNode(MassE.ScreenSetting);
	public HeliosNode boxRoot;

	public HeliosMass() {
		super("db/zui.helios", MassE.class);
		db.commit();

		initNode();
	}

	@Override
	public void wipe() {
		super.wipe();
		initNode();
	}

	private void initNode() {

		boxRoot = createJoin().addFrom(tagRoot).addFrom(tagBox).run().get(0);

		if (boxRoot == null) {
			boxRoot = new HeliosNode();
			Batch batch = new Batch().link(tagRoot, boxRoot)
					.link(tagBox, boxRoot).seal();
			executeNow(batch);
		}

		ssNode.data = getNode(ssNode.id).data;
		if (ssNode.data.length == 0) {
			ssNode.data = new ScreenSetting().toBytes();
			Batch batch = new Batch().save(ssNode).seal();
			executeNow(batch);
		}
	}

	// ==================== save ===================

	public void saveScreenSetting(byte[] data) {
		new ScreenSetting().fromBytes(data);
		ssNode.data = data;
		Batch batch = new Batch().save(ssNode).seal();
		executeNow(batch);
	}

	public byte[] getScreenSetting() {
		return ssNode.data;
	}

	private String savingPath;

	public byte[] save(HeliosNode target) {
		savingPath = getBoxAbsolutePath(target);
		MassTree tree = new MassTree();
		tree.v0830.ss.fromBytes(ssNode.data);
		saveBox(tree.v0830.root, target);
		return tree.toBytes();
	}

	private void saveBox(MassTreeN0830 boxTN, HeliosNode boxN) {

		HardItem boxHi = new HardItem();
		boxHi.fromNode(boxN);
		boxTN.name = boxHi.name;
		boxTN.box = new MassBox0830();
		if (boxHi.dataPure.length > 0) {
			boxTN.box.fromBytes(boxHi.dataPure);
		}

		Join joinA = new Join().addFrom(boxN).addFrom(tagAction);
		JoinList listA = join(joinA);
		for (HeliosNode actionN : listA) {

			MassAction0830 action = new MassAction0830();
			action.fromBytes(actionN.data);

			HeliosNode targetN = getTarget(actionN);

			if (targetN != null) {
				action.targetPath = getBoxAbsolutePath(targetN);
				if (action.targetPath.endsWith(savingPath)) {
					int idx = action.targetPath.length() - savingPath.length();
					action.targetPath = action.targetPath.substring(0, idx);
					if (action.targetPath.length() == 0)
						action.targetPath = ".";
				} else {
					action.targetPath = "";
				}
			}

			if (action.targetPath.length() == 0) {
				if (action.isInternal())
					continue;
			}

			boxTN.actionList.add(action);
		}

		saveLayer(boxTN, boxN);
	}

	private void saveLayer(MassTreeN0830 parentT, HeliosNode parentN) {
		Join joinB = new Join().addFrom(parentN).addFrom(tagBox);
		JoinList resultB = join(joinB);
		for (HeliosNode box : resultB) {
			MassTreeN0830 child = new MassTreeN0830(parentT);
			child.parent = parentT;
			parentT.childList.add(child);
			saveBox(child, box);
		}
	}

	// ====================== helper ================

	public HeliosNode getTarget(HeliosNode actionN) {
		return createJoin().addFrom(actionN).run().get(0);
	}

	// ===================== path ===============

	public String getBoxAbsolutePath(HeliosNode boxN) {
		String path = "";
		HeliosNode current = boxN;
		while (current != null) {
			String name = getBoxName(current);
			name.replace('.', '_');
			path += name + ".";
			current = getBoxParent(current);
		}
		return path;
	}

	private HeliosNode getBoxParent(HeliosNode boxN) {
		return createJoin().addTo(boxN).addFrom(tagBox).run().get(0);
	}

	private String getBoxName(HeliosNode boxN) {
		if (boxN.equals(boxRoot))
			return "";
		else
			return new HardItem().fromNode(boxN).name;
	}

	// =================== load ====================
	private String loadingPath;

	public MassTree loadToNode(HeliosNode destNode, byte[] dump) {
		loadingPath = getBoxAbsolutePath(destNode);

		MassTree tree = new MassTree();
		tree.fromBytes(dump);
		log.info("load to node(" + destNode.id + ") size=" + dump.length);

		Batch batch = new Batch();

		HardItem hiParent = new HardItem();
		if (destNode.equals(boxRoot)) {
			saveScreenSetting(tree.v0830.ss.toBytes());
			tree.v0830.root.name="";
			tree.v0830.root.box=new MassBox0830();
		} else {
			hiParent.fromNode(destNode);
			if(tree.v0830.root.name.length()==0)
				tree.v0830.root.name=hiParent.name;
		}

		hiParent.numChildren += tree.v0830.root.childList.size();
		hiParent.name = tree.v0830.root.name;
		hiParent.dataPure = tree.v0830.root.box.toBytes();
		destNode.data = hiParent.toBytes();
		batch.save(destNode);

		HashMap<String, HeliosNode> index = new HashMap<>();
		index.put(loadingPath, destNode);
		if (destNode.equals(boxRoot)) {
			for (MassTreeN0830 child : tree.v0830.root.childList) {
				loadBox(destNode, child, index, batch);
			}
			for (MassTreeN0830 child : tree.v0830.root.childList) {
				loadAction(child, index, batch);
			}
		} else {
			for (MassTreeN0830 child : tree.v0830.root.childList) {
				loadBox(destNode, child, index, batch);
			}
			loadAction(tree.v0830.root, index, batch);
		}

		batch.seal();
		executeNow(batch);

		return tree;
	}

	private void loadBox(HeliosNode parent, MassTreeN0830 ztn,
			HashMap<String, HeliosNode> index, Batch batch) {
		HeliosNode boxN = new HeliosNode();
		HardItem boxHi = new HardItem();
		boxHi.dataPure = ztn.box.toBytes();
		boxHi.name = ztn.name;
		boxHi.numChildren = ztn.childList.size();
		boxN.data = boxHi.toBytes();
		index.put(ztn.toPath() + loadingPath, boxN);
		batch.save(boxN).link(tagBox, boxN).link(parent, boxN);
		for (MassTreeN0830 child : ztn.childList) {
			loadBox(boxN, child, index, batch);
		}
	}

	private void loadAction(MassTreeN0830 ztn,
			HashMap<String, HeliosNode> index, Batch batch) {
		for (MassAction0830 action : ztn.actionList) {

			HeliosNode targetN = null;
			if (action.targetPath.length() > 0) {
				if (action.targetPath.equals("."))
					action.targetPath = loadingPath;
				else
					action.targetPath = action.targetPath + loadingPath;
				targetN = index.get(action.targetPath);
			}

			// =============== what is this about? ==============
			if (targetN != null || action.isInternal() == false) {
				String path = ztn.toPath() + loadingPath;
				HeliosNode casterN = index.get(path);
				HeliosNode actionN = createAction(batch, casterN, action);
				if (targetN != null) {
					batch.link(actionN, targetN);
				}
			}
		}

		for (MassTreeN0830 child : ztn.childList) {
			loadAction(child, index, batch);
		}
	}

	private HeliosNode createAction(Batch batch, HeliosNode casterN,
			MassAction0830 action) {
		HeliosNode actionN = new HeliosNode();
		actionN.data = action.toBytes();

		batch.save(casterN).link(tagAction, actionN).link(casterN, actionN)
				.save(actionN);
		return actionN;
	}

	public boolean isEmpty() {
		HeliosNode any = createJoin().addFrom(boxRoot).run(0, 1).get(0);
		return any == null;
	}

}
