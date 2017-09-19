package azura.zombie.logic.junior;

import java.util.HashMap;
import java.util.Queue;

import avalon.ice4.EventI;
import azura.ice.main.ZombieServer;
import azura.junior.client.JuniorInputI;
import azura.zombie.logic.P4Z;
import azura.zombie.logic.Shooter;
import azura.zombie.logic.Zombie;
import common.algorithm.FastMath;
import common.collections.Point;
import zz.junior.identity.JI枪手;

public class 枪手 extends JI枪手 implements EventI {

	public Shooter host;
	public HashMap<Zombie, P4Z> Zombie_Deal = new HashMap<>();

	public 枪手(JuniorInputI client, Shooter host) {
		super(client);
		this.host = host;
		// setLogLevel(LogLevelE.NONE);
	}

	@Override
	public void o逃跑一段() {
		host.findNearestZombie().thenAccept(z -> {
			runOnRoad(z);
			// runOnDirection(z);
			// runOnClient(z);
		});
	}

	private void runOnRoad(P4Z z) {
		Queue<Point> path = ZombieServer.roadMap.escape(host.body.x, host.body.y, z.zombie.body.x, z.zombie.body.y);
		// log.debug("escape path: size=" + path.size());
		if (path.size() == 1) {
			Point p = path.peek();
			// log.debug("escape from " + host.x + "," + host.y + " to " + p.x +
			// "," + p.y);
		}
		host.body.restartPath(path);
	}

	private void runOnClient(P4Z z) {
		host.ice.zombieS.findRun(host.body, z.zombie.body);
	}

	private void runOnDirection(P4Z z) {
		int angle = FastMath.xy2Angle(z.zombie.body.x - host.body.x, z.zombie.body.y - host.body.y);
		angle = (angle + 180) % 360;
		Point p = FastMath.angleToXy(angle, Shooter.attackRange);
		Point dest = new Point(host.body.x + p.x, host.body.y + p.y);
		int dist = (int) FastMath.dist(host.body.x, host.body.y, dest.x, dest.y);
		if (dist < 100)
			log.error("dist=" + dist);
		log.debug(
				"====================escape from " + host.body.x + "," + host.body.y + " to " + dest.x + "," + dest.y);
		host.body.restartTo(dest.x, dest.y);
	}

	@Override
	public void o标记目标僵尸() {
		host.findNearestZombie().thenAccept(deal -> {
			if (deal == null) {
				log.info("标记目标僵尸失败！！！ 最近的僵尸不存在");
				return;
			}
			if (deal.p2z == null) {
				log.info("标记目标僵尸失败！！！ 《枪手对僵尸》不存在");
				return;
			}
			deal.p2z.i通知_是目标僵尸();
		});
	}

	@Override
	public void fire(String event) {
		if (event.equals("stop")) {
			i跑完停下();
		}
	}

	@Override
	public void dispose() {
		host.body.walkEvent.removeListener(this);
		super.dispose();
	}

	@Override
	public void o枪手站立不动() {
		// host.stand();
	}

	@Override
	public void o初始化() {
		host.body.walkEvent.addListener(this, "stop");
	}

}
