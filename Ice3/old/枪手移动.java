package azura.zombie.logic.junior;

import azura.junior.client.JuniorInputI;
import azura.zombie.logic.P4Z;
import azura.zombie.logic.Shooter;
import common.collections.CompareE;
import zz.junior.script.JS枪手移动;

public class 枪手移动 extends JS枪手移动 {

	private P4Z p4z;

	public 枪手移动(JuniorInputI client, P4Z p4z) {
		super(client);
		this.p4z = p4z;
	}

	@Override
	public void o加视野外监视() {
		log.info("枪手加监视：视野外");
		p4z.shooter.addWatch(p4z.zombie, CompareE.Larger, Shooter.visualRange, () -> {
			log.info("枪手发现：僵尸在视野外");
			i僵尸在枪手视野外();
		});
	}

	@Override
	public void o加视野内监视() {
		log.info("枪手加监视：视野内");
		p4z.shooter.addWatch(p4z.zombie, CompareE.SmallerOrEqual, Shooter.visualRange, () -> {
			log.info("枪手发现：僵尸在视野内");
			i僵尸在枪手视野内();
		});
	}

	@Override
	public void o加警戒范围内监视() {
		log.info("枪手加监视：警戒范围内");
		p4z.shooter.addWatch(p4z.zombie, CompareE.SmallerOrEqual, Shooter.alertRange, () -> {
			log.info("枪手发现：僵尸在警戒范围内");
			i僵尸在枪手警戒范围内();
		});
	}

	@Override
	public void o加警戒范围外监视() {
		log.info("枪手加监视：警戒范围外");
		p4z.shooter.addWatch(p4z.zombie, CompareE.Larger, Shooter.alertRange, () -> {
			log.info("枪手发现：僵尸在警戒范围外");
			i僵尸在枪手警戒范围外();
		});
	}

	@Override
	public void o持续追僵尸() {
	}

	@Override
	public void o关于销毁我() {
	}

	@Override
	public void dispose() {
		p4z.shooter.removePair(p4z.zombie);
		super.dispose();
	}

}
