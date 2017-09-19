package azura.maze4.db;

import java.util.HashMap;

import azura.helios6.Helios6;
import azura.helios6.Hnode;
import azura.helios6.TreeN;
import azura.helios6.write.Batch;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HardItem;
import azura.maze4.handler.MazePack;
import azura.maze4.handler.RoomPack;
import zz.karma.Maze.K_Woo;

public class HeliosMaze extends Helios6<MazeE> {

	static class SingletonHolder {
		static HeliosMaze instance = new HeliosMaze();
	}

	public static HeliosMaze me() {
		return SingletonHolder.instance;
	}

	public KarmaSpace ksMaze;
	public Hnode tagRoom;
	public Hnode tagRoomRoot;
	public Hnode tagWoo;

	public HeliosMaze() {
		super("./db/maze.db", "maze", MazeE.class);
		initTags();
	}

	private void initTags() {
		tagRoom = getTagNode(MazeE.Room);
		tagRoomRoot = getTagNode(MazeE.RoomRoot);
		tagWoo = getTagNode(MazeE.Woo);
	}

	public void load(byte[] data) {
		wipe();
		MazePack mp = new MazePack();
		mp.fromBytes(data);
		TreeN up = mp.roomTree;
		up.cargo = tagRoomRoot;

		HashMap<Integer, WooCache> tid_WC = new HashMap<>();

		Batch batch = new Batch();
		writeChildren(up, mp, batch, tid_WC);

		tid_WC.values().forEach(wc -> {
			if (wc.woo.doorToTid != 0) {
				WooCache dest = tid_WC.get(wc.woo.doorToTid);
				batch.link(wc.node, dest.node);
			}
		});

		execute(batch);

	}

	private void writeChildren(TreeN up, MazePack mp, Batch batch, HashMap<Integer, WooCache> tid_WC) {
		HardItem hiUp = HardItem.fromNode(up.cargo);
		hiUp.numChildren = up.childList.size();
		up.cargo = hiUp.getNode();
		batch.save(up.cargo);
		for (TreeN child : up.childList) {
			RoomPack roomC = mp.tid_RoomPack.get(child.id);
			HardItem hiRoom = HardItem.fromNode(new Hnode());
			hiRoom.name = roomC.kr.name;
			hiRoom.data = roomC.kr.toBytes();
			Hnode roomNode = hiRoom.getNode();
			child.cargo = roomNode;
			batch.link(up.cargo, roomNode).link(tagRoom, roomNode).save(roomNode);
			roomC.wooList.forEach(woo -> {
				Hnode wooNode = new Hnode();
				WooCache wc = new WooCache();
				wc.node = wooNode;
				wc.woo = woo;
				HardItem hiWoo = HardItem.fromNode(wooNode);
				hiWoo.name = woo.name;
				hiWoo.data = woo.toBytes();
				wooNode = hiWoo.getNode();
				batch.link(roomNode, wooNode).link(tagWoo, wooNode).save(wooNode);
				tid_WC.put(woo.tid, wc);
			});
			writeChildren(child, mp, batch, tid_WC);
		}

	}

	class WooCache {
		K_Woo woo;
		Hnode node;
	}

	public byte[] save() {
		MazePack mp = new MazePack();
		mp.roomTree = getTree(tagRoom, tagRoomRoot);
		mp.roomTree.getBroadFirstList().forEach(rt -> {
			HardItem hiRoom = HardItem.fromNode(rt.cargo);
			RoomPack rp = new RoomPack();
			rp.kr.fromBytes(hiRoom.data);
			rt.id = rp.kr.tid;
			join().addFrom(rt.cargo).addFrom(tagWoo).run().forEach(wooNode -> {
				K_Woo w = new K_Woo(ksMaze);
				HardItem hiWoo = HardItem.fromNode(wooNode);
				w.fromBytes(hiWoo.data);
				rp.wooList.add(w);
			});
			mp.roomList.add(rp);
		});
		return mp.toBytes();
	}

	public Hnode wooToWoo(Hnode woo) {
		return join().addFrom(woo).addFrom(tagWoo).run().get(0);
	}

}
