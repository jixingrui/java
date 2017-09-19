package azura.maze4.handler;

import java.util.ArrayList;

import azura.karma.def.tree.Tree;
import azura.maze4.db.HeliosMaze;
import common.collections.buffer.ZintBuffer;
import common.collections.buffer.i.BytesI;
import zz.karma.Maze.K_Room;
import zz.karma.Maze.K_Woo;

public class RoomPack implements BytesI {
	public K_Room kr = new K_Room(HeliosMaze.me().ksMaze);
	public Tree tree = new Tree(0);
	public ArrayList<K_Woo> wooList = new ArrayList<>();

	public RoomPack() {
	}

	@Override
	public void fromBytes(byte[] bytes) {
		ZintBuffer zb = new ZintBuffer(bytes);
		kr.fromBytes(zb.readBytesZ());
		int size = zb.readZint();
		for (int i = 0; i < size; i++) {
			K_Woo w = new K_Woo(HeliosMaze.me().ksMaze);
			w.fromBytes(zb.readBytesZ());
			wooList.add(w);
		}
	}

	@Override
	public byte[] toBytes() {
		ZintBuffer zb = new ZintBuffer();
		zb.writeBytesZ(kr.toBytes());
		zb.writeZint(wooList.size());
		wooList.forEach(woo -> {
			zb.writeBytesZ(woo.toBytes());
		});
		return zb.toBytes();
	}
}
