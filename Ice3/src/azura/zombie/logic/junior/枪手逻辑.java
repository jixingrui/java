package azura.zombie.logic.junior;

import azura.junior.client.JuniorInputI;
import azura.zombie.logic.P4Z;
import azura.zombie.logic.Shooter;
import azura.zombie.logic.Zombie;
import zz.junior.script.JS枪手逻辑;

public class 枪手逻辑 extends JS枪手逻辑 {

	private Shooter host;

	public 枪手逻辑(JuniorInputI client, Shooter host) {
		super(client);
		this.host = host;
		// setLogLevel(LogLevelE.NONE);
	}

	@Override
	public void o生成策略枪手对僵尸() {
	}

	public void senseZombie(Zombie target) {
		// log.info("枪手感知：僵尸进入感知范围");
		P4Z deal = host.z枪手.Zombie_Deal.get(target);
		if (deal != null)
			throw new Error();

		deal = new P4Z(host, target);
		host.z枪手.Zombie_Deal.put(target, deal);
		枪手对僵尸 p2z = new 枪手对僵尸(client, deal);
		deal.p2z = p2z;
		p2z.initFuture.thenRun(() -> {
			p2z.play(p2z.r僵尸_僵尸, target.z僵尸);
			p2z.play(p2z.r枪手_枪手, host.z枪手);
		});
	}

//	public void senseZombieOut(Zombie target) {
//		// log.info("枪手感知：僵尸离开感知范围");
//		P4Z deal = host.z枪手.Zombie_Deal.get(target);
//		if (deal != null)
//			deal.dispose();
//	}
}
