package azura.zombie.logic;

import org.apache.log4j.Logger;

import azura.zombie.logic.junior.枪手对僵尸;

public class P4Z {
	private static Logger log = Logger.getLogger(P4Z.class);

	public Shooter shooter;
	public Zombie zombie;

	public 枪手对僵尸 p2z;

	public P4Z(Shooter person, Zombie zombie) {
		this.shooter = person;
		this.zombie = zombie;
	}

	public void dispose() {
		if (p2z == null)
			throw new Error();

		p2z.dispose();
		p2z = null;
		shooter.z枪手.Zombie_Deal.remove(zombie);
	}

}
