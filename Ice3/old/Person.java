package azura.zombie.logic;

import org.apache.log4j.Logger;

import azura.avalon.ice.Jumper;
import azura.avalon.ice.Walker;
import azura.ice.main.ZombieServer;
import azura.zombie.logic.junior.枪手;
import azura.zombie.service.IceS_CS;

public class Person extends Char {

	int viewWidth = 700;
	int viewHeight = 600;
	public static int visualRange = 400;
	public static int attackRange = 100;
	public static int walkSpeed = 240;

	private static Logger log = Logger.getLogger(Person.class);

	public IceS_CS ice;
	public 枪手 shooter;

	public Person(IceS_CS ice, int x, int y, int z, int angle, byte[] skin) {
		this.ice = ice;
		shooter = new 枪手(ZombieServer.juniorC.relay);

		register(ice.room, x, y, z, angle, skin, viewWidth, viewHeight);

		body.walkSpeed = walkSpeed;
	}

	@Override
	public void eyeSeeIn(Walker walker) {
		// log.info("sense walker in " + walker.getId() + " at " + walker.x +
		// "," + walker.y);
		ice.seeWalker(walker.getId(), walker.getSkin(), walker.x, walker.y, walker.angle);
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
	}

	@Override
	public void eyeSeeEvent(Jumper unit, byte[] msg) {
	}

	@Override
	public void eyeDispose() {
	}

	@Override
	public void eyeSeeChange(Walker walker) {
		ice.seeChange(walker.getId(), walker.getSkin(), walker.angle);
	}

}
