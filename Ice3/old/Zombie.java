package azura.zombie.logic;

import org.apache.log4j.Logger;

import azura.avalon.ice.Jumper;
import azura.avalon.ice.Walker;
import azura.ice.main.ZombieServer;
import azura.ice.service.ActionE;
import azura.ice.service.Form;
import azura.zombie.logic.junior.僵尸;
import azura.zombie.logic.junior.僵尸逻辑;
import azura.zombie.service.IceS_CS;
import common.algorithm.FastMath;
import common.collections.Point;

public class Zombie extends Char {

	int viewWidth = 600;
	int viewHeight = 500;
	public static int visualRange = 300;
	public static int attackRange = 50;
	public static int walkSpeed = 60;

	private static Logger log = Logger.getLogger(Zombie.class);

	public IceS_CS ice;

	// junior
	public Person target;
	public Z4P targetZ;

	public 僵尸 zombie;
	public 僵尸逻辑 logic;

	public Zombie(IceS_CS ice, int x, int y, int z, int angle, byte[] skin) {
		this.ice = ice;

		register(ice.room, x, y, z, angle, skin, viewWidth, viewHeight);
		// log.debug("create zombie id=" + body.getId());

		body.walkSpeed = walkSpeed;

		zombie = new 僵尸(ZombieServer.juniorC.relay, this);
		logic = new 僵尸逻辑(ZombieServer.juniorC.relay, this);
		logic.initFuture.thenRun(() -> {
			logic.play(logic.r僵尸_僵尸, zombie);
		});
	}

	public Z4P findNearestPerson() {
		Person nearest = super.findNearest(Person.class);
		if (nearest == null) {
			log.debug("nearest not found");
			return null;
		}

		// log.debug("nearest is found");
		target = nearest;
		targetZ = zombie.Person_Deal.get(target);
		return targetZ;
	}

	@Override
	public void eyeSeeIn(Walker walker) {
		// log.info("sense walker in " + walker.getId() + " at " + walker.x +
		// "," + walker.y);

		if ((walker.cargo instanceof Person) == false) {
			// log.debug("zombie sen zombie");
			return;
		}

		Person target = (Person) walker.cargo;

		ice.seeWalker(walker.getId(), walker.getSkin(), walker.x, walker.y, walker.angle);

		zombie.sensePerson(target);
	}

	@Override
	public void eyeSeeMove(Walker walker) {
	}

	@Override
	public void eyeSeeWalking(Walker walker) {
		ice.syncPath(walker.getId(), walker.getPath());
	}

	@Override
	public void eyeSeeOut(Walker walker) {
		// if (walker.getId() == 0)
		// log.error("Error: =========leave own sight=========");

		// log.info("sense walker out " + walker.getId());

		if ((walker.cargo instanceof Person) == false) {
			return;
		}

		Person target = (Person) walker.cargo;

		zombie.sensePersonOut(target);
	}

	@Override
	public void eyeSeeEvent(Jumper unit, byte[] msg) {
	}

	@Override
	public void eyeDispose() {
	}

	public void chase(Person person) {

		Point to = attackPos(body.x, body.y, person.body.x, person.body.y, attackRange - 10);

		ice.zombieS.findPath(this.body, to);
	}

	private Point attackPos(int xz, int yz, int xp, int yp, int attd) {

		int dist = (int) FastMath.dist(xz, yz, xp, yp);
		int xs = xp + attd * (xz - xp) / dist;
		int ys = yp + attd * (yz - yp) / dist;

		// StringBuffer sb = new StringBuffer();
		// sb.append(" xz=").append(xz);
		// sb.append(" yz=").append(yz);
		// sb.append(" xp=").append(xp);
		// sb.append(" yp=").append(yp);
		// sb.append(" attd=").append(attd);
		// sb.append(" dist=").append(dist);
		// sb.append(" xs=").append(xs);
		// sb.append(" ys=").append(ys);
		// log.info(sb.toString());

		return new Point(xs, ys);
	}

	public void bite(Person person) {
		int angle = FastMath.xy2Angle(person.body.x - body.x, person.body.y - body.y);
		body.angle = angle;

		Form f = new Form();
		f.fromBytes(body.getSkin());
		f.action = ActionE.attack;
		// body.stop();
		// body.angle
		body.setSkin(f.toBytes());
	}

	@Override
	public void eyeSeeChange(Walker walker) {
		// ice.seeChange(walker.getId(), walker.getSkin(), walker.angle);
	}

}
