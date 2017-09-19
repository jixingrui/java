package azura.zombie.logic.junior;

import azura.junior.client.JuniorInputI;
import azura.zombie.logic.Shooter;
import azura.zombie.logic.Z4P;
import azura.zombie.logic.Zombie;
import zz.junior.script.JS僵尸逻辑;

public class 僵尸逻辑 extends JS僵尸逻辑 {

	private Zombie host;

	public 僵尸逻辑(JuniorInputI client, Zombie host) {
		super(client);
		this.host = host;
		// setLogLevel(LogLevelE.NONE);
	}

	@Override
	public void o生成策略僵尸对枪手() {
	}

	public void sensePerson(Shooter person) {
		// log.info("僵尸感知：人进入感知范围");
		Z4P deal = host.z僵尸.Person_Deal.get(person);
		if (deal != null)
			throw new Error();

		deal = new Z4P(host, person);
		host.z僵尸.Person_Deal.put(person, deal);
		僵尸对枪手 z2p = new 僵尸对枪手(client, deal);
		deal.z2p = z2p;
		z2p.initFuture.thenRun(() -> {
			z2p.play(z2p.r僵尸_僵尸, host.z僵尸);
			z2p.play(z2p.r枪手_枪手, person.z枪手);
		});
	}

//	public void sensePersonOut(Shooter person) {
//		// log.info("僵尸感知：人离开感知范围");
//		Z4P deal = host.z僵尸.Person_Deal.get(person);
//		if (deal != null)
//			deal.dispose();
//	}

}
