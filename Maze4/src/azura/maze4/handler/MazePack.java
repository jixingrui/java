package azura.maze4.handler;

import java.util.ArrayList;
import java.util.HashMap;

import azura.helios6.TreeN;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;

public class MazePack implements BytesI {
	public ArrayList<RoomPack> roomList = new ArrayList<>();
	public TreeN roomTree;

	public HashMap<Integer, RoomPack> tid_RoomPack = new HashMap<>();

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeZint(roomList.size());
		roomList.forEach(rp -> {
			zb.writeBytesZ(rp.toBytes());
		});
		roomTree.writeTo(zb);
		return zb.toBytes();
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			RoomPack rp = new RoomPack();
			rp.fromBytes(zb.readBytesZ());
			roomList.add(rp);

			tid_RoomPack.put(rp.kr.tid, rp);
		}
		roomTree=new TreeN();
		roomTree.readFrom(zb);
	}
}
