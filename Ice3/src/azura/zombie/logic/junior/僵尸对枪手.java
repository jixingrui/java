package azura.zombie.logic.junior;

import azura.ice.watch.Watch;
import azura.junior.client.JuniorInputI;
import azura.zombie.logic.Z4P;
import azura.zombie.logic.Zombie;
import common.collections.CompareE;
import common.thread.TimerOne;
import zz.junior.script.JS僵尸对枪手;

public class 僵尸对枪手 extends JS僵尸对枪手 {

	private Watch 射程内监视;
	private Watch 射程外监视;
	private Z4P z4p;

	public 僵尸对枪手(JuniorInputI client, Z4P host) {
		super(client);
		this.z4p = host;
		// setLogLevel(LogLevelE.NONE);
	}

	@Override
	public void o加视野内监视() {
		// log.info("僵尸加监视：视野内");
		z4p.zombie.addWatch(z4p.person, CompareE.SmallerOrEqual, Zombie.visualRange, () -> {
			// z4p.zombie.z僵尸.i有人进入视野();
			i发现枪手在僵尸视野内();
		});
	}

	@Override
	public void o加视野外监视() {
		// log.info("僵尸加监视：视野外");
		z4p.zombie.addWatch(z4p.person, CompareE.Larger, Zombie.visualRange, () -> {
			i发现枪手在僵尸视野外();
		});
	}

	@Override
	public void o加射程内监视() {
		// log.info("僵尸加监视：射程内");
		射程内监视 = z4p.zombie.addWatch(z4p.person, CompareE.SmallerOrEqual, Zombie.attackRange, () -> {
			// log.info("僵尸发现：人在射程内");
			// i目标人在射程内();
			i发现枪手在僵尸射程内();
			// log.debug("zombie at: " + z4p.zombie.body.toString());
			// log.debug("person at: " + z4p.person.body.toString());
		});
	}

	@Override
	public void o加射程外监视() {
		射程外监视 = z4p.zombie.addWatch(z4p.person, CompareE.Larger, Zombie.attackRange, () -> {
			// i目标人在射程外();
			i发现枪手在僵尸射程外();
		});
	}

	@Override
	public void o播放咬人动画() {
		z4p.zombie.chaseStop();
		z4p.zombie.bite(z4p.person);
	}

	@Override
	public void o销毁射程监视() {
		射程内监视.dispose();
		射程外监视.dispose();
	}

	@Override
	public void o销毁我() {
		z4p.dispose();
	}

	@Override
	public void dispose() {
		z4p.zombie.removeTarget(z4p.person);
		super.dispose();
	}

	@Override
	public void o加感知外监视() {
		// in Zombie.eyeSee()
	}

	@Override
	public void o僵尸持续追枪手() {
		z4p.zombie.chase(z4p.person);
	}

	@Override
	public void o咬枪手一下() {
		TimerOne.me().schedueOnce(() -> {
			i咬完一下();
		}, 1000);
	}
}
