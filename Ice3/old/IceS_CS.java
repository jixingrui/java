package azura.zombie.service;

import azura.avalon.ice.IceRoom;
import azura.avalon.path.PathOfPoints;
import azura.ice.service.IceStore;
import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import azura.zombie.logic.Person;
import common.collections.buffer.i.OutI;
import zz.karma.Ice.K_CS;
import zz.karma.Ice.K_SC;
import zz.karma.Ice.CS.K_EnterRoom;
import zz.karma.Ice.CS.K_Move;
import zz.karma.Ice.SC.K_EnterRoomRet;
import zz.karma.Ice.SC.K_SeeChange;
import zz.karma.Ice.SC.K_SeeMove;
import zz.karma.Ice.SC.K_SeeNew;

public class IceS_CS extends K_CS {

	private OutI out;

	public int zLevel;

	public Person p;
	// public Person zz;

	public IceRoom room;

	public ZombieS_CS zombieS;

	public IceS_CS(KarmaSpace space, OutI out) {
		super(space);
		this.out = out;
	}

	// public Person createPerson(int x, int y, int angle, byte[] skin) {
	// Person p = new Person(this);
	// p.register(room, x, y, zLevel, angle, skin, 1000, 600);
	// log.debug("create person id=" + p.body.id);
	//
	//// p.body.walkSpeed = ;
	// return p;
	// }

	public void receive(byte[] data) {
		super.fromBytes(data);
		if (msg.getType() == T_EnterRoom) {
			K_EnterRoom km = new K_EnterRoom(space);
			km.fromKarma(msg);

			zLevel = km.initialPos.z;

			room = IceStore.me().getCreateRoom(km.roomUID);
			// p = createPerson(km.initialPos.x, km.initialPos.y,
			// km.initialPos.angle, km.form);
			p = new Person(this, km.initialPos.x, km.initialPos.y, zLevel, km.initialPos.angle, km.form);

			K_EnterRoomRet ret = new K_EnterRoomRet(space);
			ret.id = p.body.getId();
			sendToClient(ret.toKarma());

		} else if (msg.getType() == T_Move) {
			K_Move m = new K_Move(space);
			m.fromKarma(msg);

			PathOfPoints path = new PathOfPoints();
			path.fromBytes(m.path);

			p.body.stop();
			p.body.appendPath(path.hopList);
		}
	}

	private void sendToClient(Karma msg) {
		K_SC sc = new K_SC(space);
		sc.msg = msg;
		out.out(sc.toBytes());
	}

	public void initSpace() {
		out.out(space.toBytes());
	}

	public void seeWalker(int id, byte[] skin, int x, int y, int angle) {
		K_SeeNew sn = new K_SeeNew(space);
		sn.id = id;
		sn.pos.x = x;
		sn.pos.y = y;
		sn.pos.angle = angle;
		sn.form = skin;
		sendToClient(sn.toKarma());
	}

	public void syncPath(int id, byte[] path) {
		K_SeeMove sm = new K_SeeMove(space);
		sm.id = id;
		sm.path = path;
		sendToClient(sm.toKarma());
	}

	public void seeChange(int id, byte[] skin,int angle) {
		K_SeeChange sc=new K_SeeChange(space);
		sc.id=id;
		sc.form=skin;
		sc.angle=angle;
		sendToClient(sc.toKarma());
	}

}
