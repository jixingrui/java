package azura.karma.hard;

import java.util.List;

import azura.helios6.Helios6;
import azura.helios6.Hnode;
import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import zz.karma.Hard.K_CS;
import zz.karma.Hard.K_SC;
import zz.karma.Hard.CS.K_AskMore;
import zz.karma.Hard.CS.K_Rename;
import zz.karma.Hard.CS.K_Save;
import zz.karma.Hard.CS.K_SelectCS;
import zz.karma.Hard.SC.K_AppendDown;
import zz.karma.Hard.SC.K_ClearDown;
import zz.karma.Hard.SC.K_ClearHold;
import zz.karma.Hard.SC.K_RefillUp;
import zz.karma.Hard.SC.K_UnselectSC;
import zz.karma.Hard.SC.K_UpdateOne;

public class HardS_CS extends K_CS {

	HardCode coder;
	private int idx;
	HubScExt hub;

	public HardS_CS(KarmaSpace space, int idx, HubScExt hub, Helios6<?> db, HardHandlerI user) {
		super(space);
		this.idx = idx;
		this.hub = hub;
		coder = new HardCode(this, db, user);
		user.setHardCode(coder);
	}

	public void setRoot(Hnode root) {
		coder.setRoot(root);
	}

	// ==================== receive ==============
	public void receive(K_CS msgCS) {
		// Karma msg = cs.getKarma(F_send);
		// fromKarma(msgCS);
		send = msgCS.send;

		if (send.getType() == T_Add) {
			coder.addSH();
		} else if (send.getType() == T_SelectCS) {
			K_SelectCS k = new K_SelectCS(space);
			k.fromKarma(send);
			// boolean up_down = send.getBoolean(SelectCS.F_up_down);
			// int idx = send.getInt(SelectCS.F_idx);
			coder.selectSH(k.up_down, k.idx);
		} else if (send.getType() == T_UnselectCS) {
			coder.unselectSH();
		} else if (send.getType() == T_Rename) {
			K_Rename k = new K_Rename(space);
			k.fromKarma(send);
			// String newName = send.getString(Rename.F_name);
			coder.renameSH(k.name);
		} else if (send.getType() == T_Save) {
			K_Save k = new K_Save(space);
			k.fromKarma(send);
			// byte[] data = send.getBytes(Save.F_data);
			coder.saveSH(k.data);
		} else if (send.getType() == T_Delete) {
			coder.deleteSH();
		} else if (send.getType() == T_Hold) {
			coder.holdSH();
		} else if (send.getType() == T_Drop) {
			coder.dropSH();
		} else if (send.getType() == T_Jump) {
			coder.jumpSH();
		} else if (send.getType() == T_AskMore) {
			K_AskMore k = new K_AskMore(space);
			k.fromKarma(send);
			// int pageSize = send.getInt(AskMore.F_pageSize);
			coder.askMoreSH(k.pageSize);
		}
	}

	// ================== support =================
	private void sendSC(Karma k) {
		K_SC sc = new K_SC(space);
		sc.send = k;
		// Karma sc = new Karma(space).fromType(SC.type);
		// sc.setKarma(SC.F_send, k);
		hub.sendHard(idx, sc.toKarma());
	}

	// private Karma hiToKarma(HardItem hi) {
	// Karma itemK = new Karma(space).fromType(Item.type);
	// itemK.setString(Item.F_name, hi.name);
	// itemK.setInt(Item.F_numChildren, hi.numChildren);
	// itemK.setBytes(Item.F_data, hi.cargo);
	// return itemK;
	// }

	// ================= HS ==================

	public void clearDownHS() {
		// Karma msg = new Karma(space).fromType(ClearDown.type);
		sendSC(new K_ClearDown(space).toKarma());
	}

	public void clearHoldHS() {
		// Karma msg = new Karma(space).fromType(ClearHold.type);
		sendSC(new K_ClearHold(space).toKarma());
	}

	public void updateOneHS(boolean up_down, int idx, HardItem item) {
		K_UpdateOne k = new K_UpdateOne(space);
		k.idx = idx;
		k.up_down = up_down;
		k.item = item;
		// Karma itemK = item.getKarma();
		// Karma msg = new Karma(space).fromType(UpdateOne.type);
		// msg.setInt(UpdateOne.F_idx, idx);
		// msg.setBoolean(UpdateOne.F_up_down, up_down);
		// msg.setKarma(UpdateOne.F_item, itemK);
		sendSC(k.toKarma());
	}

	public void appendDownHS(List<HardItem> subList, boolean reachEnd) {
		K_AppendDown k = new K_AppendDown(space);
		k.end = reachEnd;
		// k.itemList=new ArrayList<>();

		// Karma msg = new Karma(space).fromType(AppendDown.type);
		// msg.setBoolean(AppendDown.F_end, reachEnd);
		// List<Karma> list = msg.getList(AppendDown.F_itemList);
		for (HardItem hi : subList) {
			// Karma k = hi.getKarma();
			// list.add(k);
			k.itemList.add(hi.toKarma());
		}
		sendSC(k.toKarma());
	}

	public void refillUpHS() {
		K_RefillUp k = new K_RefillUp(space);
		// Karma msg = new Karma(space).fromType(RefillUp.type);
		// List<Karma> list = msg.getList(RefillUp.F_itemList);
		for (HardItem hi : coder.upList) {
			// Karma k = hi.getKarma();
			// list.add(k);
			k.itemList.add(hi.toKarma());
		}
		sendSC(k.toKarma());
	}

	public void unselectHS() {
		// Karma msg = new Karma(space).fromType(UnselectSC.type);
		sendSC(new K_UnselectSC(space).toKarma());
	}

	public void deleteHS(boolean b, int selectedIdx) {
		// not implemented
	}

	// @Override
	// public void fromKarma(Karma karma) {
	// }
	//
	// @Override
	// public Karma toKarma() {
	// return null;
	// }

}
