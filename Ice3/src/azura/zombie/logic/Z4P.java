package azura.zombie.logic;

import org.apache.log4j.Logger;

import azura.zombie.logic.junior.僵尸对枪手;

public class Z4P {
	private static Logger log = Logger.getLogger(Z4P.class);

	public Zombie zombie;
	public Shooter person;

	public 僵尸对枪手 z2p;

	public Z4P(Zombie zombie, Shooter person) {
		this.zombie = zombie;
		this.person = person;
	}

	public void dispose() {
		if (z2p == null)
			throw new Error();

		z2p.dispose();
		z2p = null;
		zombie.z僵尸.Person_Deal.remove(person);
	}

}
