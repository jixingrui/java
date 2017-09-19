package azura.zombie.logic;

import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

import avalon.ice4.Body;
import avalon.ice4.Walker;
import avalon.ice4.i.SeeE;
import azura.ice.main.ZombieServer;
import azura.ice.service.ActionE;
import azura.ice.service.SpeakEvent;
import azura.zombie.logic.junior.僵尸;
import azura.zombie.logic.junior.僵尸逻辑;
import azura.zombie.service.IceS_CS;
import common.algorithm.FastMath;
import common.collections.Point;

public class Zombie extends Actor {

	public static int viewWidth = 1200;
	public static int viewHeight = 1000;
	public static int visualRange = 600;
	public static int attackRange = 40;
	// public static int walkSpeed = 60;

	private static Logger log = Logger.getLogger(Zombie.class);

	public IceS_CS ice;

	// junior
	public Shooter target;
	public Z4P targetZ;

	public 僵尸 z僵尸;
	public 僵尸逻辑 z僵尸逻辑;

	private TaskHolder th = new TaskHolder(1000);

	private volatile Walker chaseTarget;
	// private BhStrider chaseBh;
	// private ScheduledFuture<?> chaseTimer;
	// private int speed;
	// private boolean chasePause;

	public Zombie(IceS_CS ice, int x, int y, int z, int angle, byte[] skin, int speed) {
		super(ZombieServer.room, x, y, z, angle, skin, viewWidth, viewHeight);
		this.ice = ice;
		// this.speed = speed;

		body.walkSpeed = speed;

		z僵尸 = new 僵尸(ZombieServer.juniorC.relay, this);
		z僵尸逻辑 = new 僵尸逻辑(ZombieServer.juniorC.relay, this);
		z僵尸逻辑.initFuture.thenRun(() -> {
			z僵尸逻辑.play(z僵尸逻辑.r僵尸_僵尸, z僵尸);
		});
	}

	public void chase(Shooter one) {
		this.chaseTarget = one.body;

		if (ZombieServer.roadMap.canSee(body.x, body.y, chaseTarget.x, chaseTarget.y))
			body.restartTo(chaseTarget.x, chaseTarget.y);
		// body.appendTo(chaseTarget.x, chaseTarget.y);
	}

	public void chaseStop() {
		log.debug("chase stop ================");
		th.clearTask();
		chaseTarget = null;
		body.stop();
		// body.stop();
		// chaseBh = null;
		// if (chaseTimer != null)
		// chaseTimer.cancel(false);
		// chaseTimer = null;
	}

	private Point attackPos(int xz, int yz, int xp, int yp, int attd) {
		int dist = (int) FastMath.dist(xz, yz, xp, yp);
		int xs = xp + attd * (xz - xp) / dist;
		int ys = yp + attd * (yz - yp) / dist;
		return new Point(xs, ys);
	}

	public void stand() {
		// body.stop();
		// log.debug("==== stand ====");
		SpeakEvent e = new SpeakEvent();
		e.action = ActionE.stand;
		e.angle = body.angle;
		body.speak(e.toBytes());
	}

	public void bite(Shooter person) {
		// log.debug("==== bite ====");

		int angle = FastMath.xy2Angle(person.body.x - body.x, person.body.y - body.y);
		body.angle = angle;

		SpeakEvent e = new SpeakEvent();
		e.action = ActionE.attack;
		e.angle = body.angle;
		body.speak(e.toBytes());
	}

	@Override
	public void eyeSee(SeeE event, Body one) {
		super.eyeSee(event, one);
		if (event == SeeE.NEW) {
			if ((one.cargo instanceof Shooter) == false) {
				return;
			}
			Shooter target = (Shooter) one.cargo;
			ice.seeBody(one.id, one.skin, one.x, one.y, one.angle);
			z僵尸逻辑.sensePerson(target);
		} else if (event == SeeE.MOVE) {
			// log.debug("zombie moving");
		} else if (event == SeeE.LEAVE) {
			if ((one.cargo instanceof Shooter) == false) {
				return;
			}
			Shooter target = (Shooter) one.cargo;
			Z4P deal = z僵尸.Person_Deal.get(target);
			deal.z2p.i发现枪手在僵尸感知外();
			// if (deal != null)
			// deal.dispose();
			// z僵尸.sensePersonOut(target);

		}

		if (one == chaseTarget && event == SeeE.MOVE) {
			if (th.isEmpty()) {
				// log.debug("add chase task ============ ");
				th.addTask(new Runnable() {
					public void run() {
						tryDoChase();
					}

				});
			}
		}
	}

	private void tryDoChase() {
		// log.debug("try do chase ============= ");
		if (ZombieServer.roadMap.canSee(body.x, body.y, chaseTarget.x, chaseTarget.y)) {
			synchronized (chaseTarget) {
				if (chaseTarget != null)
					body.restartTo(chaseTarget.x, chaseTarget.y);
			}
		} else {
			body.stop();
		}
	}

	@Override
	public void eyeSeeSpeak(Body body, byte[] msg) {
	}

	@Override
	public void eyeSeePath(Walker walker) {
	}

	public CompletableFuture<Z4P> findNearestPerson() {
		CompletableFuture<Z4P> future = new CompletableFuture<Z4P>();
		super.findNearest(Shooter.class).thenAccept(person -> {
			if (person == null) {
				log.error("nearest not found!!!");
				future.complete(null);
				return;
			}
			target = person;
			targetZ = z僵尸.Person_Deal.get(target);
			future.complete(targetZ);
		});
		return future;
	}

	public void dispose() {
		if (targetZ != null)
			targetZ.dispose();
		z僵尸逻辑.dispose();
		// z僵尸.dispose();
	}

}
