package azura.karma.editor.hard;

import azura.helios.hard10.HardHandlerA;
import azura.helios5.HeliosNode;
import azura.karma.editor.db.KarmaE;
import azura.karma.editor.service.KarmaSC;

public class ForkHandler extends HardHandlerA {

	private KarmaSC sc;

	public ForkHandler(KarmaSC sc) {
		super(false, new HeliosNode(KarmaE.Fork));
		this.sc = sc;
	}

	@Override
	public void rename(String name) {
	}

	@Override
	public void add(byte[] data) {
		sc.addFork();
		sc.karmaChanged();
	}

	@Override
	public void save(byte[] data) {
		doSave(data);
		sc.karmaChanged();
	}

	@Override
	public void delete() {
		doDelete();
		sc.karmaChanged();
	}

	@Override
	public void select() {
	}

	@Override
	public void unselect() {
	}

	@Override
	public void drop() {
	}

}
