package azura.fractale.netty;

import org.apache.log4j.Logger;

import azura.fractale.netty.filter.ValidatorS;
import azura.fractale.netty.handler.ExceptionHandler;
import azura.fractale.netty.handler.FrackUserA;
import common.algorithm.crypto.FrackeyS;
import common.algorithm.crypto.HintBook;
import common.algorithm.crypto.RC4;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class FrackServerA {
	protected final Logger log;

	public abstract FrackUserA newUser();

	private HintBook book;

	public FrackServerA() {
		log = Logger.getLogger(this.getClass());
	}

	// /**
	// * The server is constructed only once on its boot date. The hint does not
	// * change afterwards.
	// */
	// public FrackServer(byte[] book) {
	// this.book = new HintBook(book);
	// log = Logger.getLogger(this.getClass());
	// }
	//
	// public FrackServer(String bookPath) {
	// this(HintBook.readBookFromImage(bookPath));
	// }
	public void listen(int port, String bookPath) {
		listen(port, HintBook.readBookFromImage(bookPath));
	}

	public void listen(int port, byte[] bookData) {
		this.book = new HintBook(bookData);

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap sb = new ServerBootstrap();
			// sb.option(ChannelOption.TCP_NODELAY, false)
			sb.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							FrackUserA user = newUser();
							byte[] hint = book.getHint();
							RC4 rc4 = new RC4(book.getKey(hint));
							FrackeyS fks = new FrackeyS(hint, rc4);
							ValidatorS vs = new ValidatorS(user, fks);

							ch.pipeline().addLast(vs).addLast(new ExceptionHandler());
						}
					});

			// System.out.println("listening on: " + port);
			log.info("listening on: " + port);
			ChannelFuture f = sb.bind(port).sync();
			f.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			log.error("failed listening on: " + port, e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
