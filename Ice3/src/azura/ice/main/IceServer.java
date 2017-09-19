package azura.ice.main;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import azura.fractale.netty.FrackServerA;
import azura.fractale.netty.handler.FrackUserA;
import azura.fractale.tool.FrackTool;
import azura.ice.service.IceNet;
import azura.karma.def.KarmaSpace;
import common.net.FlashPolicyService;

public class IceServer {

	public static Logger log = Logger.getLogger(IceServer.class);

	public static ExecutorService pool = Executors.newFixedThreadPool(1);

	private static byte[] book;

	private static KarmaSpace ksIce;

	public static void main(String[] args) throws IOException {

		args = new String[] { "7131" };

		int portArg = 0;
		try {
			portArg = Integer.parseInt(args[0]);
		} catch (Exception e) {
			log.error("java -jar xxx.jar port");
			return;
		}

		final int port = portArg;
		log.info("server start: " + port);

		FlashPolicyService.start();
		FrackTool.initLogger("assets/log4j.properties");
		book = FrackTool.readCodeBook("assets/homura.png");
		ksIce = new KarmaSpace().fromFile("assets/Ice.k2");

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
				return new IceNet(ksIce);
			}
		};
		server.listen(port, book);
	}

}
