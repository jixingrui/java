package azura.karma.editor.service;

import java.util.concurrent.CompletableFuture;

import azura.expresso.Datum;
import azura.expresso.rpc.phoenix13.TunnelI;
import azura.helios.hard10.HardHandlerA;
import azura.helios.hard10.HardHub;
import azura.helios.hard10.HardItem;
import azura.helios5.HeliosNode;
import azura.helios5.batch.Batch;
import azura.karma.def.KarmaSpace;
import azura.karma.editor.db.HeliosKarma;
import azura.karma.editor.def.KarmaDef;
import azura.karma.editor.def.KarmaTooth;
import azura.karma.editor.hard.FieldHandler;
import azura.karma.editor.hard.ForkHandler;
import azura.karma.editor.hard.KarmaHandler;
import azura.karma.editor.hard.KarmaHardE;
import azura.karma.editor.sdk.SDK_As;
import azura.karma.editor.sdk.SDK_Java;
import azura.phoenix13.drop.karma.param.Arg1155Hint;
import azura.phoenix13.drop.karma.param.Arg1159Hint;
import azura.phoenix13.drop.karma.param.Arg1167Hint;
import azura.phoenix13.drop.karma.param.Arg1185Hint;
import azura.phoenix13.drop.karma.param.Ret1164Hint;
import azura.phoenix13.drop.karma.param.Ret1175Hint;
import azura.phoenix13.drop.karma.param.Ret1179Hint;
import azura.phoenix13.drop.karma.service.editSCA;
import common.algorithm.FastMath;
import common.collections.buffer.i.ZintReaderI;
import common.util.ZipUtil;

public class KarmaSC extends editSCA implements TunnelI {

	public HardHub<KarmaHardE> hub;
	public KarmaHandler karmaHandler;
	public HardHandlerA fieldHandler;
	public HardHandlerA forkHandler;

	private HardItem focusedKarma;

	public KarmaSC(TunnelI tunnel) {
		super(KarmaNet.nsKarma, tunnel);

		hub = new HardHub<KarmaHardE>(HeliosKarma.singleton(), this, KarmaHardE.class);
		karmaHandler = new KarmaHandler(this);
		hub.register(KarmaHardE.karma, karmaHandler);
		fieldHandler = new FieldHandler(this);
		hub.register(KarmaHardE.field, fieldHandler);
		forkHandler = new ForkHandler(this);
		hub.register(KarmaHardE.fork, forkHandler);
	}

	@Override
	protected void hardHandler(Datum arg1155) {
		byte[] data = arg1155.getBean(Arg1155Hint.msg).asBytes();
		hub.receive(data);
	}

	@Override
	protected void selectHandler() {
		focusedKarma = karmaHandler.getSelectedItem();
		fieldHandler.root = focusedKarma.getNode(true);
		fieldHandler.clearDown();
		fieldHandler.searchDown(true);

		Datum arg1185 = ns.newDatum(Arg1185Hint.CLASS);
		arg1185.getBean(Arg1185Hint.name).setString(karmaHandler.getSelectedPath());
		selectedIsCall(arg1185);
	}

	public void addFork() {

		if (karmaHandler.getSelectedItem() == null)
			return;

		HeliosNode field = fieldHandler.getSelectedItem().getNode(false);
		HeliosNode target = karmaHandler.getSelectedItem().getNode(false);

		HardItem hiTarget = new HardItem().fromNode(target);
		KarmaDef targetDef = new KarmaDef();
		targetDef.fromBytes(hiTarget.dataPure);

		// check exist
		int size = HeliosKarma.singleton().createJoin().addFrom(field).addTo(target).run().size();
		if (size > 0)
			return;

		// create
		HardItem hi = new HardItem().fromNode(new HeliosNode());
		hi.name = karmaHandler.getSelectedItem().name;

		KarmaTooth tooth = new KarmaTooth();
		tooth.targetName = hi.name;
		tooth.targetType = targetDef.tid;
		hi.dataPure = tooth.toBytes();
		HeliosNode forkN = hi.getNode(true);

		Batch batch = new Batch().link(field, forkN).link(HeliosKarma.singleton().tagFork, forkN).link(forkN, target)
				.save(forkN).seal();
		HeliosKarma.singleton().executeNow(batch);

		showFork();
	}

	public void showFork() {
		HeliosNode field = fieldHandler.getSelectedItem().getNode(false);

		forkHandler.root = field;
		forkHandler.clearDown();
		forkHandler.searchDown(true);
	}

	@Override
	protected void saveHandler(CompletableFuture<Datum> ret1164Sink) {

		HardItem selectedItem = karmaHandler.getSelectedItem();
		HeliosNode selected = selectedItem.getNode(true);
		selected = HeliosKarma.singleton().getNode(selected.id);

		KarmaSpace space = HeliosKarma.singleton().genSpace(selected);
		byte[] k2 = space.toBytes();

		ZipUtil zip = new ZipUtil();
		zip.appendFile(selectedItem.name + ".k2", k2);
		SDK_Java.genByProject(space, zip);
		SDK_As.genByProject(space, zip);

		Datum value = ns.newDatum(Ret1164Hint.CLASS);
		value.getBean(Ret1164Hint.pack).setBytes(zip.toBytes());
		ret1164Sink.complete(value);
	}

	@Override
	protected void loadHandler(Datum arg1167) {
		byte[] zip = arg1167.getBean(Arg1167Hint.pack).asBytes();

		KarmaSpace space = new KarmaSpace();
		space.fromBytes(zip);

		fieldHandler.clearDown();

		HeliosKarma.singleton().loadKarma(space);
		karmaHandler.clearDown();
		karmaHandler.searchDown(true);

	}

	@Override
	protected void wipeHandler() {
		HeliosKarma.singleton().wipe();
		karmaHandler.clearDown();
	}

	@Override
	protected void javaHandler(CompletableFuture<Datum> ret1175Sink) {

		HeliosNode selected = karmaHandler.getSelectedItem().getNode(false);

		KarmaSpace space = HeliosKarma.singleton().genSpace(selected);

		byte[] pack = SDK_Java.genByProject(space);

		Datum ret = ns.newDatum(Ret1175Hint.CLASS);
		ret.getBean(Ret1175Hint.pack).setBytes(pack);
		ret1175Sink.complete(ret);
	}

	@Override
	protected void as3Handler(CompletableFuture<Datum> ret1179Sink) {

		HeliosNode selected = karmaHandler.getSelectedItem().getNode(false);

		KarmaSpace tree = HeliosKarma.singleton().genSpace(selected);

		byte[] pack = SDK_As.genByProject(tree);

		Datum ret = ns.newDatum(Ret1179Hint.CLASS);
		ret.getBean(Ret1179Hint.pack).setBytes(pack);
		ret1179Sink.complete(ret);
	}

	@Override
	public void connected() {
	}

	@Override
	public void disconnected() {
	}

	@Override
	public void tunnelOut(ZintReaderI reader) {
		Datum arg1159 = ns.newDatum(Arg1159Hint.CLASS);
		arg1159.getBean(Arg1159Hint.msg).setBytes(reader.toBytes());
		hardCall(arg1159);
	}

	public void karmaChanged() {

		byte[] data = focusedKarma.dataPure;

		KarmaDef kdc = new KarmaDef();
		kdc.fromBytes(data);
		kdc.versionSelf = FastMath.tidInt();
		// kdc.note += "\nver=" + kdc.versionSelf;

		focusedKarma.dataPure = kdc.toBytes();

		HeliosNode focusedNode = focusedKarma.getNode(true);

		Batch batch = new Batch().save(focusedNode).seal();

		HeliosKarma.singleton().executeNow(batch);

		// HeliosNode selectedNode =
		// karmaHandler.getSelectedItem().getNode(false);S
		// karmaHandler.unselect_();
		// karmaHandler.save(kdc.toBytes());
	}

	public void updateFocus(HardItem selectedItem) {
		focusedKarma = selectedItem;
	}

}
