package azura.zombie.logic.junior;

import azura.ice.watch.Watch;
import azura.junior.client.JuniorInputI;
import azura.zombie.logic.P4Z;
import azura.zombie.logic.Shooter;
import common.collections.CompareE;
import common.thread.TimerOne;
import zz.junior.script.JS枪手对僵尸;

public class 枪手对僵尸 extends JS枪手对僵尸 {

	private P4Z p4z;
	private Watch 射程内监视;
	private Watch 射程外监视;

	public 枪手对僵尸(JuniorInputI client, P4Z p4z) {
		super(client);
		this.p4z = p4z;
		// setLogLevel(LogLevelE.NONE);
	}

	@Override
	public void o加射程外监视() {
		// log.info("枪手加监视：射程外");
		射程外监视 = p4z.shooter.addWatch(p4z.zombie, CompareE.Larger, Shooter.attackRange, () -> {
			// log.info("枪手发现：僵尸在射程外");
			i发现僵尸在枪手射程外();
			// i僵尸在射程外();
		});
	}

	@Override
	public void o加射程内监视() {
		// log.info("枪手加监视：射程内");
		射程内监视 = p4z.shooter.addWatch(p4z.zombie, CompareE.SmallerOrEqual, Shooter.attackRange, () -> {
			// log.info("枪手发现：僵尸在射程内");
			// i僵尸在射程内();
			i发现僵尸在枪手射程内();
		});
	}

	@Override
	public void o加视野外监视() {
		// log.info("枪手加监视：视野外");
		p4z.shooter.addWatch(p4z.zombie, CompareE.Larger, Shooter.visualRange, () -> {
			// log.info("枪手发现：僵尸在视野外");
			// i僵尸在枪手视野外();
			i发现僵尸在视野外();
		});
	}

	@Override
	public void o加视野内监视() {
		// log.info("枪手加监视：视野内");
		p4z.shooter.addWatch(p4z.zombie, CompareE.SmallerOrEqual, Shooter.visualRange, () -> {
			// log.info("枪手发现：僵尸在视野内");
			// i僵尸在枪手视野内();
			i发现僵尸在视野内();
		});
	}

	@Override
	public void o枪手持续追僵尸() {
		// p4z.shooter.chase(p4z.zombie);
		// log.info("todo: o枪手持续追僵尸"); z4p.zombie.chase(z4p.person);
	}

	@Override
	public void o枪手打僵尸一下() {
		TimerOne.me().schedueOnce(() -> {
			i枪手打完一下();
		}, 1000);
	}

	@Override
	public void o加警戒范围内监视() {
		// log.info("枪手加监视：警戒范围内");
		p4z.shooter.addWatch(p4z.zombie, CompareE.SmallerOrEqual, Shooter.alertRange, () -> {
			// log.info("枪手发现：僵尸在警戒范围内");
			// i僵尸在枪手警戒范围内();
			i发现僵尸在警戒范围内();
		});
	}

	@Override
	public void o加警戒范围外监视() {
		// log.info("枪手加监视：警戒范围外");
		p4z.shooter.addWatch(p4z.zombie, CompareE.Larger, Shooter.alertRange, () -> {
			// log.info("枪手发现：僵尸在警戒范围外");
			// i僵尸在枪手警戒范围外();
			i发现僵尸在警戒范围外();
		});
	}

	@Override
	public void o播放枪击动画() {
		p4z.shooter.attack();
	}

	@Override
	public void o销毁射程监视() {
		if (射程外监视 == null || 射程内监视 == null) {
			log.error("=========== o销毁射程监视:监视不存在 ===========");
			return;
		}

		射程外监视.dispose();
		射程内监视.dispose();
		射程外监视 = null;
		射程内监视 = null;
	}

	@Override
	public void o销毁我() {
		p4z.dispose();
	}

	@Override
	public void dispose() {
		p4z.shooter.removeTarget(p4z.zombie);
		super.dispose();
	}

	@Override
	public void o加感知外监视() {
		// in Shooter.eyeSee()
	}

}
