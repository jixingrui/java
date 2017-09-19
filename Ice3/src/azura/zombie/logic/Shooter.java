package azura.zombie.logic;

import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

import avalon.ice4.Body;
import avalon.ice4.Walker;
import avalon.ice4.i.SeeE;
import azura.ice.main.ZombieServer;
import azura.ice.service.ActionE;
import azura.ice.service.SpeakEvent;
import azura.zombie.logic.junior.枪手;
import azura.zombie.logic.junior.枪手逻辑;
import azura.zombie.service.IceS_CS;
import common.algorithm.FastMath;

public class Shooter extends Actor {

	public static int viewWidth = 1400;
	public static int viewHeight = 1200;
	public static int visualRange = 600;
	public static int attackRange = 400;
	// public static int walkSpeed = 240;
	public static int alertRange = 150;

	private static Logger log = Logger.getLogger(Shooter.class);

	public IceS_CS ice;
	public 枪手 z枪手;
	private 枪手逻辑 z枪手逻辑;

	Zombie target;
	private P4Z targetP;

	public Shooter(IceS_CS ice, int x, int y, int z, int angle, byte[] skin, int speed) {
		super(ZombieServer.room, x, y, z, angle, skin, viewWidth, viewHeight);
		body.walkSpeed = speed;

		this.ice = ice;
		z枪手 = new 枪手(ZombieServer.juniorC.relay, this);
		z枪手逻辑 = new 枪手逻辑(ZombieServer.juniorC.relay, this);
		z枪手逻辑.initFuture.thenRun(() -> {
			z枪手逻辑.play(z枪手逻辑.r枪手_枪手, z枪手);
		});
	}

	@Override
	public void eyeSee(SeeE event, Body one) {
		super.eyeSee(event, one);
		if (event == SeeE.NEW) {
			ice.seeBody(one.id, one.skin, one.x, one.y, one.angle);
			if ((one.cargo instanceof Zombie) == false) {
				return;
			}
			Zombie target = (Zombie) one.cargo;
			ice.seeBody(one.id, one.skin, one.x, one.y, one.angle);
			z枪手逻辑.senseZombie(target);
		} else if (event == SeeE.MOVE) {
			// log.debug("see move "+one.x+","+one.y);
			if (one.cargo instanceof Zombie)
				ice.seeMove(one.id, one.x, one.y, one.angle);
		} else if (event == SeeE.STOP) {
			// log.debug("eye see stop");
			ice.seeStop(one.id, one.x, one.y, one.angle);
		} else if (event == SeeE.SKIN) {
			ice.seeChange(one.id, one.skin, one.angle);
		} else if (event == SeeE.LEAVE) {
			if ((one.cargo instanceof Zombie) == false) {
				return;
			}
			Zombie target = (Zombie) one.cargo;
			// z枪手逻辑.senseZombieOut(target);
			P4Z deal = z枪手.Zombie_Deal.get(target);
			// if (deal != null)
			// deal.dispose();
			deal.p2z.i发现僵尸在枪手感知外();
		}
	}

	@Override
	public void eyeSeeSpeak(Body one, byte[] msg) {
		ice.seeSpeak(one.id, msg);
	}

	@Override
	public void eyeSeePath(Walker walker) {
		ice.syncPath(walker.id, walker.path.toBytes());
	}

	public void die() {
		SpeakEvent e = new SpeakEvent();
		e.action = ActionE.die;
		e.angle = body.angle;
		body.speak(e.toBytes());
	}

	public void attack() {
		// log.debug("==== shoot ====");

		int angle = FastMath.xy2Angle(target.body.x - body.x, target.body.y - body.y);
		body.angle = angle;
		// body.setSkin(body.skin);

		SpeakEvent e = new SpeakEvent();
		e.action = ActionE.attack;
		e.angle = body.angle;
		body.speak(e.toBytes());
	}

	public void stand() {
		log.debug("==== stand ====");
		SpeakEvent e = new SpeakEvent();
		e.action = ActionE.stand;
		e.angle = body.angle;
		body.speak(e.toBytes());
	}

	public CompletableFuture<P4Z> findNearestZombie() {
		CompletableFuture<P4Z> future = new CompletableFuture<P4Z>();
		super.findNearest(Zombie.class).thenAccept(zombie -> {
			if (zombie == null) {
				log.error("nearest not found!!!");
				future.complete(null);
				return;
			}
			target = zombie;
			targetP = z枪手.Zombie_Deal.get(target);
			future.complete(targetP);
		});
		return future;
	}

	public void dispose() {
		if (targetP != null)
			targetP.dispose();
		z枪手逻辑.dispose();
		// z枪手.dispose();
	}
}
