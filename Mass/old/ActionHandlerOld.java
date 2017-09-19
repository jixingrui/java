package azura.banshee.zui.service;

import java.util.concurrent.CompletableFuture;

import azura.banshee.zui.helios.ZuiHelios;
import azura.banshee.zui.helios.ZuiHeliosE;
import azura.banshee.zui.model.ZuiAction;
import azura.helios.hard10.HardHandlerA;
import azura.helios.hard10.HardItem;
import azura.helios.hard10.ie.HardCSE;
import azura.helios5.HeliosNode;
import azura.helios5.batch.Batch;
import azura.helios5.join.Join;

public class ActionHandlerOld extends HardHandlerA {

	private ZuiSc sc;
	private HeliosNode tagB = new HeliosNode(ZuiHeliosE.box);

	public ActionHandlerOld(ZuiSc sc) {
		super(false, new HeliosNode(ZuiHeliosE.action), HardCSE.delete_m,
				HardCSE.rename_m, HardCSE.add_m, HardCSE.save_m);
		this.sc = sc;
	}

	@Override
	public void batchOnAdd(HeliosNode newNode, CompletableFuture<Boolean> commit) {
		commit.complete(true);
	}

	@Override
	public void batchOnSave(byte[] oldData, byte[] newData,
			CompletableFuture<Boolean> commit) {
		commit.complete(true);
	}

	@Override
	public void batchOnDelete(HeliosNode target,
			CompletableFuture<Boolean> commit) {
		commit.complete(true);
	}

	@Override
	public void onSelect() {
	}

	@Override
	public void onUnselect() {
	}

	@Override
	public void onDrop() {
		HardItem box = sc.hub.get(ZuiHardE.box).getSelectedItem();
		if (box == null) {
			return;
		}

		if (heldItem == getSelectedItem()) {
			HeliosNode heldActionNode = heldItem.getNode(false);
			Join join = new Join().addFrom(heldActionNode).addFrom(tagB);
			HeliosNode toState = ZuiHelios.singleton().join(join).get(0);
			if (toState != null) {
				ZuiAction action = new ZuiAction();
				action.fromBytes(heldItem.data);
				action.targetStateName = "";
				action.targetStateId = 0;
				heldItem.data = action.toBytes();
				heldActionNode = heldItem.getNode(true);

				Batch batch = new Batch().delink(heldActionNode, toState)
						.save(heldActionNode).seal();
				ZuiHelios.singleton().executeNow(batch);
				update();
				
//				sc.hub.get(ZuiHardE.box).update();
			}

			return;
		}

		ZuiAction action = new ZuiAction();
		action.fromBytes(heldItem.data);
		action.targetStateName = box.name;
		action.targetStateId = box.getNode(false).id;

		// log.info("activates " + box.name);

		heldItem.data = action.toBytes();

		HeliosNode actionNode = heldItem.getNode(false);

		Batch batch = new Batch().save(heldItem.getNode(true));

		Join joinT = new Join().addFrom(actionNode);
		HeliosNode oldTarget = ZuiHelios.singleton().join(joinT).get(0);

		if (oldTarget != null) {
			batch.delink(actionNode, oldTarget);
		}

		batch.link(actionNode, box.getNode(false)).seal();

		ZuiHelios.singleton().executeNow(batch);

	}

}
