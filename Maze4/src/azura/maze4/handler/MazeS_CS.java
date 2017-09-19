package azura.maze4.handler;

import azura.karma.def.KarmaSpace;
import azura.karma.hard.HubScExt;
import azura.karma.run.Karma;
import azura.maze4.db.HeliosMaze;
import zz.karma.Maze.K_CS;
import zz.karma.Maze.K_SC;
import zz.karma.Maze.CS.K_Load;
import zz.karma.Maze.SC.K_SaveRet;

public class MazeS_CS extends K_CS {

	private HubScExt hub;

	public MazeS_CS(KarmaSpace space, HubScExt hub) {
		super(space);
		this.hub = hub;
	}

	private void send(Karma k) {
		K_SC sc = new K_SC(space);
		sc.msg.fromKarma(k);
		hub.sendCustom(sc.toBytes());
	}

	public void receive(byte[] bytes) {
		fromBytes(bytes);
		if (msg.getType() == T_Save) {
			save();
		} else if (msg.getType() == T_Load) {
			K_Load l = new K_Load(space);
			l.fromKarma(msg);
			load(l);
		}else if(msg.getType()==T_Clear){
			clear();
		}
	}

	private void clear() {
		HeliosMaze.me().wipe();
		hub.reloadAll();
	}

	private void load(K_Load L) {
		HeliosMaze.me().load(L.data);
		hub.reloadAll();
	}

	private void save() {
		K_SaveRet sr = new K_SaveRet(space);
		sr.mazeData = HeliosMaze.me().save();
		send(sr.toKarma());

	}

}
