package azura.banshee.zui.service;

import azura.banshee.zui.helios.HeliosMass;
import azura.banshee.zui.helios.MassE;
import azura.helios.hard10.HardHandlerA;
import azura.helios5.HeliosNode;

public class BoxHandler extends HardHandlerA {

	private MassSc sc;

	public BoxHandler(MassSc sc) {
		super(true, new HeliosNode(MassE.box));
		super.root = HeliosMass.singleton().boxRoot;
		this.sc = sc;
	}

	@Override
	public void rename(String name) {
		name = name.replace(".", "_");
		doRename(name);
	}

	@Override
	public void save(byte[] data) {
		doSave(data);
	}

	@Override
	public void delete() {
		HeliosNode target = getSelectedItem().getNode(false);
		sc.deleteActionByTarget(target);
		doDelete();
	}

	@Override
	public void select() {
	}

	@Override
	public void unselect() {
	}

	@Override
	public void hold() {
		super.hold();
		sc.capture();
	}

	@Override
	public void drop() {
		swapId();
	}

	@Override
	public void add(byte[] data) {
	}

}
