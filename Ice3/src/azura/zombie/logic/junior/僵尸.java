package azura.zombie.logic.junior;

import java.util.HashMap;

import azura.junior.client.JuniorInputI;
import azura.zombie.logic.Shooter;
import azura.zombie.logic.Z4P;
import azura.zombie.logic.Zombie;
import zz.junior.identity.JI僵尸;

public class 僵尸 extends JI僵尸 {

	private Zombie host;
	public HashMap<Shooter, Z4P> Person_Deal = new HashMap<>();

	public 僵尸(JuniorInputI client, Zombie zombie) {
		super(client);
		this.host = zombie;
		// setLogLevel(LogLevelE.NONE);
	}

	@Override
	public void o僵尸站立不动() {
		host.chaseStop();
	}

	@Override
	public void o标记目标枪手() {
		host.findNearestPerson().thenAccept(deal -> {
			if (deal == null) {
				log.info("标记目标人失败！！！ 最近的人不存在");
				return;
			}
			if (deal.z2p == null) {
				log.info("标记目标人失败！！！ 《僵尸对人》不存在");
				return;
			}
			deal.z2p.i通知_是目标枪手();
		});
	}

}
