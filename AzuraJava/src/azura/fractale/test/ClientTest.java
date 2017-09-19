package azura.fractale.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import azura.fractale.netty.FrackClientA;
import azura.fractale.netty.handler.FrackUserA;

import common.algorithm.Stega;
import common.algorithm.crypto.HintBook;

public class ClientTest {

	public static void main(String[] args) throws InterruptedException,
			IOException {

		BufferedImage bookImg = ImageIO.read(new File("homura.png"));
		byte[] book = Stega.decode(bookImg, HintBook.dataLength);

		FrackClientA fc = new FrackClientA(book) {

			@Override
			public FrackUserA newUser() {
				return new SimpleClient();
			}
		};
		fc.connect("juno", 8888);
	}

}
