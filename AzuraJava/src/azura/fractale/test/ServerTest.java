package azura.fractale.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.fractale.netty.FrackServerA;
import azura.fractale.netty.handler.FrackUserA;

import common.algorithm.Stega;
import common.algorithm.crypto.HintBook;

public class ServerTest {

	public static void main(String[] args) throws InterruptedException,
			IOException {

		BufferedImage bookImg = ImageIO.read(new File("homura.png"));
		byte[] book = Stega.decode(bookImg, HintBook.dataLength);

		FrackServerA fs = new FrackServerA() {

			@Override
			public FrackUserA newUser() {
				return new SimpleServer();
			}
		};
		fs.listen(8888,book);
	}

}
