package azura.junior;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import azura.helios6.Hnode;
import azura.junior.db.HeliosJunior3;
import azura.junior.db.JuniorHardE;
import azura.junior.server.JuniorEditorS_CS;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HubScExt;
import azura.karma.hard.server.HokServer;
import azura.karma.hard.server.HokUserI;

public class Junior3Edit implements HokUserI {

	public static void main(String[] args) throws IOException {
		args = new String[] { "7211" };
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("java -jar xxx.jar port");
			return;
		}

		readLoggerDef("./assets/log4j.properties");
		readKarmaDef("./assets/JuniorEdit_v25.k2");
		shutdownService();

		new Junior3Edit(port);
	}

	private static void readKarmaDef(String path) {
		HeliosJunior3.ksJuniorEdit = new KarmaSpace().fromFile(path);
		// JuniorEditorS_CS.ksJunior = ksJunior;
	}

	private static void shutdownService() {
		new Thread(() -> {
			Logger.getLogger(Junior3Edit.class)
					.debug("Press ENTER to call System.exit() and run the shutdown routine.");
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}).start();
	}

	private static void readLoggerDef(String path) {
		PropertyConfigurator.configure(path);
		Logger.getLogger("io.netty").setLevel(Level.WARN);
		Logger.getLogger("org.apache.http").setLevel(Level.WARN);
	}

	// private static KarmaSpace ksJunior;
	// ================ class ============

	private HokServer hok;
	private HubScExt hub;
	private JuniorEditorS_CS sc;

	public HardCenter center = new HardCenter();

	public Junior3Edit(int port) {
		hok = new HokServer(this, HeliosJunior3.me());
		hok.readHardDef("./assets/Hard_v31.k2");
		hok.listen(port, "./assets/homura.png");
	}

	@Override
	public void initHard(HubScExt hub) {
		this.hub = hub;

		sc = new JuniorEditorS_CS(HeliosJunior3.ksJuniorEdit, this, center);
		hub.sendCustom(HeliosJunior3.ksJuniorEdit.toBytes());

		hub.register(JuniorHardE.script, center.scriptHandler);
		hub.register(JuniorHardE.role, center.roleHandler);
		hub.register(JuniorHardE.roleCopy, center.roleCopyHandler);
		hub.register(JuniorHardE.concept, center.conceptHandler);
		hub.register(JuniorHardE.conceptCopy, center.conceptCopyHandler);
		hub.register(JuniorHardE.trigger, center.triggerHandler);
		hub.register(JuniorHardE.cause, center.causeHandler);
		hub.register(JuniorHardE.profession, center.identityHandler);
	}

	public void reloadHardAll() {
		hub.reloadAll();
		center.clear();
	}

	@Override
	public void receiveCustom(byte[] cargo) {
		sc.receive(cargo);
	}

	@Override
	public void sendCustom(byte[] cargo) {
		hub.sendCustom(cargo);
	}

	public byte[] report() {
		Hnode concept = center.conceptHandler.hc.selectedItem.getNode();
		Hnode mind = center.currentMind();
//		String result = HeliosJunior3.me().reportIdea(mind, concept);
		return HeliosJunior3.me().drawRelation(mind,concept);
//		return result;
	}

//	public String testRun() {
//		return center.scriptHandler.testRun();
//	}

}
