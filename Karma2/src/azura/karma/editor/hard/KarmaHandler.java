package azura.karma.editor.hard;

import azura.helios.hard10.HardHandlerA;
import azura.helios5.HeliosNode;
import azura.karma.editor.db.HeliosKarma;
import azura.karma.editor.db.KarmaE;
import azura.karma.editor.def.KarmaDef;
import azura.karma.editor.service.KarmaSC;

import common.algorithm.FastMath;

public class KarmaHandler extends HardHandlerA {

	private KarmaSC sc;

	public KarmaHandler(KarmaSC sc) {
		super(true, new HeliosNode(KarmaE.Karma));
		super.root = HeliosKarma.singleton().tagKarmaRoot;
		this.sc = sc;
	}

	@Override
	public void add(byte[] data) {
		KarmaDef karma = new KarmaDef();
		karma.tid = FastMath.tidInt();
		karma.versionSelf = FastMath.tidInt();
		karma.name = "karma" + FastMath.random(1, 99);

		HeliosNode karmaN = doAdd(karma.name, karma.toBytes());
		HeliosKarma.singleton().indexKarma(karma.tid, karmaN.id);
	};

	@Override
	public void rename(String name) {

		name = name.replace('.', '_');
		boolean forkUpdated = HeliosKarma.singleton().updateFork(
				getSelectedItem().getNode(false), name);
		if (forkUpdated) {
			sc.fieldHandler.clearDown();
		}
		doRename(name);

		KarmaDef kdc = new KarmaDef();
		kdc.fromBytes(getSelectedItem().dataPure);
		kdc.name = name;
		kdc.versionSelf = FastMath.tidInt();

		doSave(kdc.toBytes());

		sc.updateFocus(getSelectedItem());
	}

	@Override
	public void save(byte[] data) {

		KarmaDef kdc = new KarmaDef();
		kdc.fromBytes(data);
		kdc.versionSelf = FastMath.tidInt();
		data = kdc.toBytes();

		doSave(data);

		sc.updateFocus(getSelectedItem());
	}

	@Override
	public void delete() {
	}

	@Override
	public void select() {
	}

	@Override
	public void unselect() {
	}

	@Override
	public void drop() {
		swapId();
	}

}
