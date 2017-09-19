package azura.banshee.zui.service;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

import azura.banshee.zui.helios.HeliosMass;
import azura.banshee.zui.model.MassBoxPropE;
import azura.banshee.zui.model.MassTree;
import azura.banshee.zui.model.v2016083001.ActionToE0830;
import azura.banshee.zui.model.v2016083001.ByActionE0830;
import azura.banshee.zui.model.v2016083001.MassAction0830;
import azura.banshee.zui.model.v2016083001.MassBox0830;
import azura.expresso.Datum;
import azura.expresso.rpc.phoenix13.TunnelI;
import azura.helios.hard10.HardHandlerA;
import azura.helios.hard10.HardHub;
import azura.helios.hard10.HardItem;
import azura.helios5.HeliosNode;
import azura.helios5.batch.Batch;
import azura.helios5.join.Join;
import azura.helios5.join.JoinList;
import azura.phoenix13.drop.zui.param.Arg1073Hint;
import azura.phoenix13.drop.zui.param.Arg1077Hint;
import azura.phoenix13.drop.zui.param.Arg1085Hint;
import azura.phoenix13.drop.zui.param.Arg1098Hint;
import azura.phoenix13.drop.zui.param.Arg1102Hint;
import azura.phoenix13.drop.zui.param.Arg1106Hint;
import azura.phoenix13.drop.zui.param.Arg1121Hint;
import azura.phoenix13.drop.zui.param.Arg1133Hint;
import azura.phoenix13.drop.zui.param.Arg1149Hint;
import azura.phoenix13.drop.zui.param.Ret1082Hint;
import azura.phoenix13.drop.zui.param.Ret1096Hint;
import azura.phoenix13.drop.zui.param.Ret1126Hint;
import azura.phoenix13.drop.zui.service.zuiSCA;

import common.collections.buffer.i.ZintReaderI;
import common.util.FileUtil;

public class MassSc extends zuiSCA implements TunnelI {

	public HardHub<MassHardE> hub;
	public HardHandlerA boxHandler;

	private HeliosNode heldBox;
	private HeliosNode actionNode;
	private MassAction0830 tempAction;

	public MassSc(TunnelI tunnel) {
		super(MassNet.nsZui, tunnel);

		hub = new HardHub<MassHardE>(HeliosMass.singleton(), this,
				MassHardE.class);
		boxHandler = new BoxHandler(this);
		hub.register(MassHardE.box, boxHandler);
	}

	@Override
	protected void hardHandler(Datum arg1073) {
		byte[] msg = arg1073.getBean(Arg1073Hint.msg).asBytes();
		hub.receive(msg);
	}

	@Override
	public void tunnelOut(ZintReaderI reader) {
		Datum arg1077 = ns.newDatum(Arg1077Hint.CLASS);
		arg1077.getBean(Arg1077Hint.msg).setBytes(reader.toBytes());
		hardCall(arg1077);
	}

	@Override
	public void connected() {
	}

	@Override
	public void disconnected() {
	}

	// =================== action =======================

	public void capture() {
		heldBox = null;
		tempAction = null;
		actionNode = null;

		HardItem hi = boxHandler.getHeldItem();
		if (hi != null)
			heldBox = hi.getNode(false);
	}

	@Override
	protected void selectByActionHandler(Datum arg1098) {
		int typeV = arg1098.getBean(Arg1098Hint.type).asInt();
		ByActionE0830 type = ByActionE0830.values()[typeV];
		tempAction = new MassAction0830();
		actionNode = getActionNode(type);
		if (actionNode != null) {
			tempAction.fromBytes(actionNode.data);
			tempAction.targetPath = loadActionTarget(actionNode);
		} else {
			tempAction.byType = type;
		}

		tellAction(tempAction);
	}

	@Override
	protected void selectToActionHandler(Datum arg1133) {
		int type = arg1133.getBean(Arg1133Hint.type).asInt();
		tempAction.toType = ActionToE0830.values()[type];

		if (actionNode != null) {
			actionNode.data = tempAction.toBytes();
			saveActionNode(actionNode);

			checkDeleteActionNode();
		} else if (!tempAction.isEmpty()) {
			actionNode = createActionNode();
		}

		tellAction(tempAction);
	}

	@Override
	protected void setTargetHandler() {

		HeliosNode targetNew = null;
		HardItem selectedBox = boxHandler.getSelectedItem();
		if (selectedBox != null)
			targetNew = selectedBox.getNode(false);

		HeliosNode targetOld = null;
		if (actionNode != null)
			targetOld = getTarget(actionNode);

		if (targetOld == null && targetNew != null) {
			if (actionNode == null)
				actionNode = createActionNode();
			Batch b = new Batch().link(actionNode, targetNew).seal();
			HeliosMass.singleton().executeNow(b);

			tempAction.targetPath = HeliosMass.singleton().getBoxAbsolutePath(
					targetNew);
			actionNode.data = tempAction.toBytes();
			saveActionNode(actionNode);
			tellAction(tempAction);
		} else if (targetOld != null && targetNew != null) {
			if (targetOld.equals(targetNew))
				return;

			Batch b = new Batch();
			if (targetOld != null)
				b.delink(actionNode, targetOld);
			if (targetNew != null)
				b.link(actionNode, targetNew);
			b.seal();
			HeliosMass.singleton().executeNow(b);

			tempAction.targetPath = HeliosMass.singleton().getBoxAbsolutePath(
					targetNew);
			actionNode.data = tempAction.toBytes();
			saveActionNode(actionNode);
			tellAction(tempAction);
		} else if (targetOld != null && targetNew == null) {

			tempAction.targetPath = "";
			actionNode.data = tempAction.toBytes();

			Batch b = new Batch().delink(actionNode, targetOld)
					.save(actionNode).seal();
			HeliosMass.singleton().executeNow(b);

			checkDeleteActionNode();
			tellAction(tempAction);
		} else {
			return;
		}

	}

	/*
	 * When int message or string message is typed in. Could be empty.
	 */
	@Override
	protected void saveMsgHandler(Datum arg1106) {
		byte[] ad = arg1106.getBean(Arg1106Hint.action).asBytes();
		MassAction0830 temp = new MassAction0830();
		temp.fromBytes(ad);

		tempAction.intMsg = temp.intMsg;
		tempAction.stringMsg = temp.stringMsg;

		if (actionNode == null && !tempAction.isEmpty()) {
			actionNode = createActionNode();
		} else if (actionNode != null && !tempAction.isEmpty()) {
			actionNode.data = tempAction.toBytes();
			saveActionNode(actionNode);
		} else if (actionNode != null && tempAction.isEmpty()) {
			checkDeleteActionNode();
		} else {
			return;
		}

	}

	// ====================== support ====================

	private void saveActionNode(HeliosNode actionN) {
		Batch batch = new Batch().save(actionN).seal();
		HeliosMass.singleton().executeNow(batch);
	}

	private void checkDeleteActionNode() {
		if (!tempAction.isEmpty())
			return;

		HeliosNode target = getTarget(actionNode);

		Batch batch = new Batch().delink(HeliosMass.singleton().tagAction,
				actionNode);
		if (target != null)
			batch.delink(actionNode, target);
		batch.delink(heldBox, actionNode).delete(actionNode).seal();
		HeliosMass.singleton().executeNow(batch);

		actionNode = null;
		tempAction.targetPath = "";
	}

	private HeliosNode createActionNode() {

		HeliosNode actionN = new HeliosNode();
		actionN.data = tempAction.toBytes();

		Batch batch = new Batch()
		.link(HeliosMass.singleton().tagAction, actionN)
		.link(heldBox, actionN).save(actionN).seal();
		HeliosMass.singleton().executeNow(batch);

		return actionN;
	}

	private HeliosNode getActionNode(ByActionE0830 type) {
		JoinList actionList = HeliosMass.singleton().createJoin()
				.addFrom(heldBox).addFrom(HeliosMass.singleton().tagAction)
				.run();
		for (HeliosNode actionN : actionList) {
			MassAction0830 action = new MassAction0830();
			action.fromBytes(actionN.data);
			if (action.byType == type) {
				return actionN;
			}
		}
		return null;
	}

	private void tellAction(MassAction0830 action) {
		Datum arg1102 = ns.newDatum(Arg1102Hint.CLASS);
		arg1102.getBean(Arg1102Hint.action).setBytes(action.toBytes());
		tellActionCall(arg1102);
	}

	private String loadActionTarget(HeliosNode actionN) {
		HeliosNode targetN = HeliosMass.singleton().getTarget(actionN);
		return HeliosMass.singleton().getBoxAbsolutePath(targetN);
	}

	private HeliosNode getTarget(HeliosNode actionN) {
		return HeliosMass.singleton().createJoin().addFrom(actionN).run()
				.get(0);
	}

	private HeliosNode getFocusBox() {
		HeliosNode targetNode;
		HardItem target = boxHandler.getSelectedItem();
		if (target == null) {
			targetNode = HeliosMass.singleton().boxRoot;
		} else {
			targetNode = target.getNode(false);
		}
		return targetNode;
	}

	// ======================= save =======================

	@Override
	protected void saveHandler(CompletableFuture<Datum> ret1082Sink) {
		HeliosNode targetNode = getFocusBox();

		// dump zui tree
		byte[] dump = HeliosMass.singleton().save(targetNode);
		// log.info("dumped size=" + dump.length + " md5="
		// + MD5.bytesToString(dump));
		Datum value = ns.newDatum(Ret1082Hint.CLASS);
		value.getBean(Ret1082Hint.dump).setBytes(dump);
		ret1082Sink.complete(value);
	}

	@Override
	protected void loadHandler(Datum arg1085) {
		boolean isEmpty = HeliosMass.singleton().isEmpty();

		byte[] dump = arg1085.getBean(Arg1085Hint.dump).asBytes();
		// log.info("load size=" + dump.length + " md5=" +
		// MD5.bytesToString(dump));
		HeliosNode targetNode = getFocusBox();
		MassTree tree = HeliosMass.singleton().loadToNode(targetNode, dump);

		if (isEmpty) {
			HeliosMass.singleton().saveScreenSetting(tree.v0830.ss.toBytes());
			Datum arg1149 = ns.newDatum(Arg1149Hint.CLASS);
			arg1149.getBean(Arg1149Hint.data).setBytes(tree.v0830.ss.toBytes());
			tellScreenSettingCall(arg1149);
		}

		boxHandler.clearDown();
		boxHandler.searchDown(true);
	}

	@Override
	protected void wipeHandler() {
		HeliosMass.singleton().wipe();
		boxHandler.clearDown();
	}

	// ======================= report ========================
	@Override
	protected void reportActionHandler(CompletableFuture<Datum> ret1096Sink) {
		StringBuilder sb = new StringBuilder();
		HeliosNode target = getFocusBox();
		reportSingle(sb, target);
		byte[] data = sb.toString().getBytes(Charset.forName("utf8"));
		data = FileUtil.compress(data);
		Datum report = ns.newDatum(Ret1096Hint.CLASS);
		report.getBean(Ret1096Hint.report).setBytes(data);
		ret1096Sink.complete(report);
	}

	private void reportSingle(StringBuilder sb, HeliosNode target) {
		sb.append("== ")
		.append(HeliosMass.singleton().getBoxAbsolutePath(target))
		.append(" ==\r\n");

		MassBox0830 box = new MassBox0830();
		HardItem boxHi = new HardItem();
		boxHi.fromNode(target);
		if (boxHi.dataPure.length > 0) {
			box.fromBytes(boxHi.dataPure);
		}
		if (box.propSet.get(MassBoxPropE.RECEIVER.ordinal())) {
			sb.append("\t[r](").append(box.from_device).append(")")
			.append(box.from_mass).append("\r\n");
		}

		Join join = new Join().addFrom(target).addFrom(
				HeliosMass.singleton().tagAction);
		JoinList list = HeliosMass.singleton().join(join);
		for (HeliosNode actionN : list) {
			MassAction0830 za = new MassAction0830();
			za.fromBytes(actionN.data);
			za.targetPath = loadActionTarget(actionN);
			if (!za.isEmpty())
				sb.append("\t").append(za.toString()).append("\r\n");
			else
				log.error("empty action found during report");
		}

		reportChildList(sb, target);
	}

	private void reportChildList(StringBuilder sb, HeliosNode parent) {
		JoinList list = HeliosMass.singleton().createJoin().addFrom(parent)
				.addFrom(HeliosMass.singleton().tagBox).run();
		for (HeliosNode child : list) {
			reportSingle(sb, child);
		}
	}

	@Override
	protected void setScreenSettingHandler(Datum arg1121) {
		byte[] data = arg1121.getBean(Arg1121Hint.data).asBytes();
		HeliosMass.singleton().saveScreenSetting(data);
	}

	@Override
	protected void getScreenSettingHandler(CompletableFuture<Datum> ret1126Sink) {
		byte[] data = HeliosMass.singleton().getScreenSetting();
		Datum ret = ns.newDatum(Ret1126Hint.CLASS);
		ret.getBean(Ret1126Hint.data).setBytes(data);
		ret1126Sink.complete(ret);
	}

	public void deleteActionByTarget(HeliosNode target) {
		HeliosNode ta = HeliosMass.singleton().tagAction;
		JoinList jr = HeliosMass.singleton().createJoin().addTo(target)
				.addFrom(ta).run();
		for (HeliosNode action : jr) {
			HeliosMass.singleton().deleteByForce(action);
		}
	}

}
