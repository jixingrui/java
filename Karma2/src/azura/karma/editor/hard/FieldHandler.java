package azura.karma.editor.hard;

import azura.helios.hard10.HardHandlerA;
import azura.helios5.HeliosNode;
import azura.helios5.join.JoinList;
import azura.karma.editor.db.HeliosKarma;
import azura.karma.editor.db.KarmaE;
import azura.karma.editor.def.KarmaField;
import azura.karma.editor.service.KarmaSC;
import azura.karma.run.bean.BeanTypeE;
import common.algorithm.FastMath;
import common.collections.buffer.ZintBuffer;

public class FieldHandler extends HardHandlerA {

	private KarmaSC sc;

	public FieldHandler(KarmaSC sc) {
		super(false, new HeliosNode(KarmaE.Field));
		this.sc = sc;
	}

	@Override
	public void rename(String name) {
		doRename(name);
//		sc.karmaChanged();
	}

	@Override
	public void add(byte[] data) {
		KarmaField field = new KarmaField();
		field.id = FastMath.tidInt();
		field.type = BeanTypeE.EMPTY;
		String name = "field" + FastMath.random(1, 99);
		ZintBuffer zb = new ZintBuffer();
		field.writeTo(zb);
		doAdd(name, zb.toBytes());

//		sc.karmaChanged();
	}

	@Override
	public void save(byte[] data) {
		doSave(data);

//		sc.karmaChanged();
	}

	@Override
	public void delete() {

		HeliosNode tagFork = HeliosKarma.singleton().tagFork;
		HeliosNode selected = getSelectedItem().getNode(false);

		JoinList jr = HeliosKarma.singleton().createJoin().addFrom(selected)
				.addFrom(tagFork).run();
		for (HeliosNode teeth : jr) {
			HeliosKarma.singleton().deleteByForce(teeth);
		}

		super.doDelete();

//		sc.karmaChanged();
	}

	@Override
	public void select() {
		sc.showFork();
	}

	@Override
	public void unselect() {
		sc.forkHandler.clearDown();
	}

	@Override
	public void drop() {
		swapId();
//		sc.karmaChanged();
	}

}
