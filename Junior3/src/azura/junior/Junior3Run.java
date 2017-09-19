package azura.junior;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import azura.fractale.netty.FrackServerA;
import azura.fractale.netty.handler.FrackUserA;
import azura.junior.db.HeliosJunior3;
import azura.junior.engine.JuniorSpace;
import azura.junior.engine.def.JuniorDef;
import azura.junior.server.JuniorRunNet;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HardItem;
import common.net.FlashPolicyService;

public class Junior3Run extends FrackServerA {

	// private static KarmaSpace space;
	private static JuniorDef def;
	private static JuniorSpace js;

	public static void main(String[] args) throws IOException {
		args = new String[] { "7212" };
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("java -jar xxx.jar port");
			return;
		}

		initLogger("./assets/log4j.properties");
		shutdownService();
		FlashPolicyService.start();

		HeliosJunior3.ksJuniorRun = new KarmaSpace().fromFile("./assets/JuniorRun.k2");
		HeliosJunior3.ksJuniorEdit = new KarmaSpace().fromFile("./assets/JuniorEdit_v15.k2");
		HardItem.ksHard = new KarmaSpace().fromFile("./assets/Hard_v30.k2");

		def = HeliosJunior3.me().genEngine();
		js = new JuniorSpace(def);

		new Junior3Run().listen(port, "./assets/homura.png");
	}

	private static void shutdownService() {
		new Thread(() -> {
			Logger.getLogger(Junior3Run.class).debug("Press ENTER to call System.exit() and run the shutdown routine.");
			try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}).start();
		;
	}

	private static void initLogger(String path) {
		PropertyConfigurator.configure(path);
		Logger.getLogger("io.netty").setLevel(Level.WARN);
		Logger.getLogger("org.apache.http").setLevel(Level.WARN);
	}

	// ================ class ============

	@Override
	public FrackUserA newUser() {
		return new JuniorRunNet(HeliosJunior3.ksJuniorRun, js);
	}

}
