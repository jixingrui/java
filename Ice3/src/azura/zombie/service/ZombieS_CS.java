package azura.zombie.service;

import java.util.ArrayList;
import java.util.List;

import avalon.ice4.Walker;
import azura.avalon.path.PathOfPoints;
import azura.ice.service.ActionE;
import azura.ice.service.Form;
import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import azura.zombie.logic.Shooter;
import azura.zombie.logic.Zombie;
import common.algorithm.FastMath;
import common.collections.Point;
import common.collections.buffer.i.OutI;
import common.collections.buffer.i.ZintReaderI;
import common.util.Session;
import zz.karma.Zombie.K_ZombieCS;
import zz.karma.Zombie.K_ZombieSC;
import zz.karma.Zombie.ZombieCS.K_CreateNpc;
import zz.karma.Zombie.ZombieCS.K_EscapeRet;
import zz.karma.Zombie.ZombieCS.K_FindPathRet;
import zz.karma.Zombie.ZombieCS.K_IceCS;
import zz.karma.Zombie.ZombieCS.K_MazeInfo;
import zz.karma.Zombie.ZombieSC.K_Escape;
import zz.karma.Zombie.ZombieSC.K_FindPath;
import zz.karma.Zombie.ZombieSC.K_IceSC;

public class ZombieS_CS extends K_ZombieCS implements OutI {

	private OutI net;
	private IceS_CS ice;
	private List<Zombie> zombieList = new ArrayList<>();

	public ZombieS_CS(KarmaSpace ksZombie, KarmaSpace ksIce, OutI net) {
		super(ksZombie);
		this.net = net;

		ice = new IceS_CS(ksIce, this);
		ice.zombieS = this;
	}

	public void receive(ZintReaderI reader) {
		// log.debug(Thread.currentThread().getName());
		super.fromBytes(reader.toBytes());
		if (msg.getType() == T_IceCS) {
			K_IceCS ki = new K_IceCS(space);
			ki.fromKarma(msg);
			ice.receive(ki.msg);
		} else if (msg.getType() == T_CreateNpc) {
			K_CreateNpc cn = new K_CreateNpc(space);
			cn.fromKarma(msg);
			Form f = new Form();
			f.skin = cn.shape;
			f.action = ActionE.stand;
			f.speed = FastMath.random(5, 9) * 10;
			// f.speed = 60;
			Zombie zombieP = new Zombie(ice, cn.startPos.x, cn.startPos.y, ice.zLevel, cn.startPos.angle, f.toBytes(),
					f.speed);
			zombieList.add(zombieP);
			// log.info("create npc id=" + zombieP.body.getId());
		} else if (msg.getType() == T_MazeInfo) {
			K_MazeInfo mi = new K_MazeInfo(space);
			mi.fromKarma(msg);
			// byte[] data = mi.data;
			// Zway way=new Zway();
			// way.fromBytes(mi.data);

			// ice.load(mi.data);
			// Logger.getLogger(getClass()).info("maze info received: " +
			// data.length / 1000 + "kb");
		} else if (msg.getType() == T_FindPathRet) {
			K_FindPathRet fp = new K_FindPathRet(space);
			fp.fromKarma(msg);
			sb.happen(fp.session, fp);
			// PathOfPoints pop = new PathOfPoints();
			// pop.fromBytes(fp.path);
			// log.info("zombie at " + zombieP.body.x + "" + zombieP.body.y);
			// log.info(pop.toString());
			// // zombieP.ice.syncPath(zombieP.body.id, fp.path);
			// zombieP.body.stop();
			// zombieP.body.appendPath(pop.hopList);
		} else if (msg.getType() == T_EscapeRet) {
			K_EscapeRet rdr = new K_EscapeRet(space);
			rdr.fromKarma(msg);
			sf.happen(rdr.session, rdr);
		}
	}

	@Override
	public void out(byte[] data) {
		K_IceSC ics = new K_IceSC(space);
		ics.msg = data;
		sendToClient(ics.toKarma());
	}

	private void sendToClient(Karma k) {
		K_ZombieSC sc = new K_ZombieSC(space);
		sc.msg = k;
		net.out(sc.toBytes());
	}

	public void initIce() {
		ice.initSpace();
	}

	private Session<K_FindPathRet> sb = new Session<K_FindPathRet>();

	public void findPath(Walker walker, Point to) {
		int sid = sb.plan(fpr -> {
			PathOfPoints pop = new PathOfPoints();
			pop.fromBytes(fpr.path);
			log.info("walk along path: " + pop.toString());
			// walker.stop();
			walker.restartPath(pop.hopList);
		});
		K_FindPath fp = new K_FindPath(space);
		fp.session = sid;
		fp.xStart = walker.x;
		fp.yStart = walker.y;
		fp.xEnd = to.x;
		fp.yEnd = to.y;
		sendToClient(fp.toKarma());
	}

	private Session<K_EscapeRet> sf = new Session<K_EscapeRet>();

	public void findRun(Walker runner, Walker monster) {
		int sid = sf.plan(rdr -> {
			PathOfPoints pop = new PathOfPoints();
			pop.fromBytes(rdr.path);
			if (pop.hopList.isEmpty()) {
				log.error("nowhere to run");
				return;
			}
			runner.restartPath(pop.hopList);
			// log.debug("run to: " + rdr.x + "," + rdr.y);
			// runner.walkTo(rdr.x, rdr.y);
		});
		K_Escape rd = new K_Escape(space);
		rd.session = sid;
		// rd.angle = angle;
		rd.xRunner = runner.x;
		rd.yRunner = runner.y;
		rd.xMonster = monster.x;
		rd.yMonster = monster.y;
		rd.dist = Shooter.attackRange;
		sendToClient(rd.toKarma());
	}

	public void dispose() {
		ice.dispose();
		zombieList.forEach(z -> {
			z.dispose();
		});
	}
}
