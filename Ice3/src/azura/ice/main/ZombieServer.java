package azura.ice.main;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;

import avalon.ice4.IceRoom4;
import azura.banshee.zbase.station.RoadMap;
import azura.fractale.netty.FrackServerA;
import azura.fractale.netty.handler.FrackUserA;
import azura.fractale.tool.FrackTool;
import azura.junior.client.run.JuniorC;
import azura.junior.client.run.JuniorS;
import azura.junior.db.HeliosJunior3;
import azura.junior.engine.JuniorSpace;
import azura.junior.engine.def.JuniorDef;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HardItem;
import azura.zombie.logic.junior.初始化说明;
import azura.zombie.service.ZombieNet;
import common.net.FlashPolicyService;
import common.util.FileUtil;

public class ZombieServer {

	public static Logger log = Logger.getLogger(ZombieServer.class);

	public static ExecutorService pool = Executors.newFixedThreadPool(1, new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "ZombieServer");
		}
	});

	private static byte[] book;

	private static KarmaSpace ksIce;

	private static KarmaSpace ksZombie;

	private static KarmaSpace ksJuniorRun;

	public static JuniorDef zombieJ;

	public static JuniorSpace zombieSpaceJ;

	private static 初始化说明 init;

	public static JuniorS juniorS;

	public static JuniorC juniorC;

	public static IceRoom4 room;
	public static RoadMap roadMap;

	public static IceRoom4 getRoom(int chamberSize, int z, byte[] baseData) {
		if (room == null) {
			room = new IceRoom4(chamberSize, z);
			roadMap = new RoadMap();
			roadMap.fromBytes(baseData);
		}
		return room;
	}

	public static KarmaSpace ksIce4;

	public static void main(String[] args) throws IOException {

		args = new String[] { "7131" };

		int portArg = 0;
		try {
			portArg = Integer.parseInt(args[0]);
		} catch (Exception e) {
			log.error("java -jar xxx.jar port");
			return;
		}

		FrackTool.initLogger("assets/log4j.properties");

		final int port = portArg;
		log.info("server start: " + port);

		FlashPolicyService.start();
		book = FrackTool.readCodeBook("assets/homura.png");
		ksIce = new KarmaSpace().fromFile("assets/Ice.k2");
		ksIce4 = new KarmaSpace().fromFile("assets/Ice4.k2");
		ksZombie = new KarmaSpace().fromFile("assets/Zombie.k2");
		ksJuniorRun = new KarmaSpace().fromFile("assets/JuniorRun.k2");
		HardItem.ksHard = new KarmaSpace().fromFile("assets/Hard_v31.k2");
		HeliosJunior3.ksJuniorEdit = new KarmaSpace().fromFile("assets/JuniorEdit_v25.k2");

		// room=new IceRoom4(ksIce, chamberSize, z)

		byte[] jdb = FileUtil.read("assets/Junior.jdb");

		HeliosJunior3.me().loadDb(jdb);
		zombieJ = HeliosJunior3.me().genEngine();
		zombieSpaceJ = new JuniorSpace(zombieJ);

		LocalTunnel tunnel = new LocalTunnel();
		juniorS = new JuniorS(ksJuniorRun, zombieSpaceJ, tunnel);
		juniorC = new JuniorC(ksJuniorRun, tunnel);
		tunnel.c = juniorC;
		tunnel.s = juniorS;

		// juniorEmb = new JuniorEmbeddedOld(zombieSpaceJ);

		init = new 初始化说明(juniorC.relay);

		// Thread.currentThread().start();

		pool.execute(new Runnable() {

			@Override
			public void run() {
				init(book, port);
			}
		});

	}

	private static void init(byte[] book, int port) {
		final FrackServerA server = new FrackServerA() {

			@Override
			public FrackUserA newUser() {
				return new ZombieNet(ksZombie, ksIce);
			}
		};
		server.listen(port, book);
	}

}
