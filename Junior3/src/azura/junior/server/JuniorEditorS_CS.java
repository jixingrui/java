package azura.junior.server;

import java.nio.charset.Charset;

import azura.helios6.Hnode;
import azura.junior.HardCenter;
import azura.junior.Junior3Edit;
import azura.junior.db.HeliosJunior3;
import azura.junior.sdk.SDK_Java;
import azura.karma.def.KarmaSpace;
import azura.karma.run.Karma;
import common.algorithm.FastMath;
import common.util.FileUtil;
import common.util.ZipUtil;
import zz.karma.JuniorEdit.K_CS;
import zz.karma.JuniorEdit.K_Idea;
import zz.karma.JuniorEdit.K_SC;
import zz.karma.JuniorEdit.CS.K_Load;
import zz.karma.JuniorEdit.CS.K_SaveIdea;
import zz.karma.JuniorEdit.SC.K_ReportIdeaRet;
import zz.karma.JuniorEdit.SC.K_SaveRet;
import zz.karma.JuniorEdit.SC.K_SelectIdeaRet;
import zz.karma.JuniorEdit.SC.K_TestRunRet;

public class JuniorEditorS_CS extends K_CS {
	// private static final Logger log = Logger.getLogger(JuniorSC.class);

	private Junior3Edit js;
	private HardCenter center;

	public JuniorEditorS_CS(KarmaSpace space, Junior3Edit js, HardCenter center) {
		super(space);
		this.js = js;
		this.center = center;
	}

	public void receive(byte[] cargo) {
		super.fromBytes(cargo);
		if (send.getType() == T_Save) {
			save();
			js.reloadHardAll();
		} else if (send.getType() == T_Wipe) {
			HeliosJunior3.me().wipe();
			js.reloadHardAll();
		} else if (send.getType() == T_Load) {
			K_Load load = new K_Load(space);
			load.fromKarma(send);
			HeliosJunior3.me().loadDb(load.db);
			js.reloadHardAll();
		} else if (send.getType() == T_Sdk) {
			genSdk();
		} else if (send.getType() == T_ReportRelation) {
			report();
		} else if (send.getType() == T_TestRun) {
			testRun();
		} else if (send.getType() == T_SelectIdea) {
			// log.info("select idea");
			selectIdea();
		} else if (send.getType() == T_SaveIdea) {
			// log.info("save idea");
			K_SaveIdea si = new K_SaveIdea(space);
			si.fromKarma(send);
			center.conceptHandler.saveIdea(si.idea);
		}

	}

	private void selectIdea() {
		Hnode idea = center.idea;
		if (idea == null)
			throw new Error();
		K_Idea ki = new K_Idea(space);
		ki.fromBytes(idea.getData());
		K_SelectIdeaRet ks = new K_SelectIdeaRet(space);
		ks.idea = ki;
		sendHubSC(ks.toKarma());
	}

	private void save() {
		ZipUtil zip = new ZipUtil();
		byte[] db = HeliosJunior3.me().saveDb();

		zip.appendFile("junior.jdb", db);
		SDK_Java.genSdk(zip);

		K_SaveRet sr = new K_SaveRet(space);
		sr.db = zip.toBytes();
		sr.version = FastMath.tidInt();
		sendHubSC(sr.toKarma());
	}

	private void genSdk() {
		// K_SdkRet sr = new K_SdkRet(space);
		// sr.version = FastMath.tidInt();
		// sr.data = SDK_Java.genSdk();
		// sendHubSC(sr.toKarma());
	}

	public void sendHubSC(Karma msg) {
		K_SC sc = new K_SC(space);
		sc.send = msg;
		js.sendCustom(sc.toBytes());
	}

	private void report() {
		// String txt = js.report();
		// if (txt == null)
		// return;

		// byte[] data = txt.getBytes(Charset.forName("UTF-8"));
		// data = FileUtil.compress(data);

		K_ReportIdeaRet rir = new K_ReportIdeaRet(space);
		rir.data = js.report();

		sendHubSC(rir.toKarma());
	}

	private void testRun() {
		String txt = center.scriptHandler.testRun();
		// log.debug(txt);
		byte[] data = txt.getBytes(Charset.forName("UTF-8"));
		data = FileUtil.compress(data);

		K_TestRunRet ret = new K_TestRunRet(space);
		ret.data = data;

		sendHubSC(ret.toKarma());
	}

}
