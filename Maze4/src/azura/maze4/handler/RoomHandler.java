package azura.maze4.handler;

import azura.helios6.Hnode;
import azura.helios6.write.Batch;
import azura.karma.hard.HardCode;
import azura.karma.hard.HardHandlerI;
import azura.karma.hard.HardItem;
import azura.maze4.Maze4Edit;
import azura.maze4.db.HeliosMaze;
import azura.maze4.db.MazeHardE;
import common.algorithm.FastMath;
import zz.karma.Maze.K_Room;

public class RoomHandler implements HardHandlerI {

	private HardCode hc;

	@Override
	public Hnode getTagNode() {
		return HeliosMaze.me().tagRoom;
	}

	@Override
	public void setHardCode(HardCode hc) {
		this.hc = hc;
		hc.setRoot(HeliosMaze.me().tagRoomRoot);
	}

	@Override
	public boolean isTree() {
		return true;
	}

	@Override
	public void add() {
		K_Room rd = new K_Room(Maze4Edit.ksMaze);
		rd.tid = FastMath.tidInt();

		Batch batch = new Batch();
		Hnode newRoom = new Hnode();

		HardItem hiRoom = HardItem.fromNode(newRoom);
		hiRoom.name = "room" + FastMath.random(1, 99);
		rd.name = hiRoom.name;
		hiRoom.data = rd.toBytes();

		hc.doAdd(batch, hiRoom);
		HeliosMaze.me().execute(batch);
		hc.doAddRefresh();
	}

	@Override
	public void rename(String name) {
		boolean success = hc.doRename(name);
		if (success == false)
			return;

		HardItem hiRoom = hc.selectedItem;
		K_Room kr = new K_Room(Maze4Edit.ksMaze);
		kr.fromBytes(hiRoom.data);
		kr.name = name;
		hiRoom.data = kr.toBytes();

		Batch b = new Batch().save(hiRoom.getNode());
		HeliosMaze.me().execute(b);
	}

	@Override
	public void delete() {
		Batch batch = hc.doDeleteBefore();
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
		Hnode selected = hc.selectedItem.getNode();
		hc.getHC(MazeHardE.Woo).setRoot(selected);
	}

	@Override
	public void notifyUnselect() {
		hc.getHC(MazeHardE.Woo).setRoot(null);
	}

	@Override
	public void notifyRefreshRelatedAll() {
	}

	@Override
	public void notifyRefreshRelatedRoot() {
	}

}
