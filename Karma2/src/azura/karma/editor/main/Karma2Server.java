package azura.karma.editor.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import azura.fractale.netty.FrackServerA;
import azura.fractale.netty.handler.FrackUserA;
import azura.karma.editor.service.KarmaNet;
import common.algorithm.Stega;
import common.algorithm.crypto.HintBook;
import common.net.FlashPolicyService;

public class Karma2Server {

	public static Logger log = Logger.getLogger(Karma2Server.class);

	public static ExecutorService pool = Executors.newFixedThreadPool(1);

	public static void main(String[] args) throws IOException {

		initLogger();

		args = new String[] { "7516" };

		int portArg = 0;
		try {
			portArg = Integer.parseInt(args[0]);
		} catch (Exception e) {
			log.error("java -jar maze.jar port");
			return;
		}

		final int port = portArg;
		log.info("server start: " + port);

		FlashPolicyService.start();

		BufferedImage bookImg = ImageIO.read(new File("assets/homura.png"));
		final byte[] book = Stega.decode(bookImg, HintBook.dataLength);

		pool.execute(new Runnable() {

			@Override
			public void run() {
				init(book, port);
			}
		});

	}

	private static void initLogger() {
		PropertyConfigurator.configure("log4j.properties");
		Logger.getLogger("io.netty").setLevel(Level.WARN);
		Logger.getLogger("org.apache.http").setLevel(Level.WARN);
	}

	private static void init(byte[] book, int port) {
		final FrackServerA server = new FrackServerA() {

			@Override
			public FrackUserA newUser() {
				return new KarmaNet();
			}
		};
		server.listen(port, book);
	}

}
