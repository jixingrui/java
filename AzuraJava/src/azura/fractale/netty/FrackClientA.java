package azura.fractale.netty;

import org.apache.log4j.Logger;

import azura.fractale.netty.filter.ValidatorC;
import azura.fractale.netty.handler.FrackUserA;
import common.algorithm.crypto.FrackeyC;
import common.algorithm.crypto.HintBook;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public abstract class FrackClientA {
	protected final Logger log;

	public abstract FrackUserA newUser();

	private final HintBook book;

	private Channel channel;

	public FrackClientA(byte[] codeBook) {
		this.book = new HintBook(codeBook);
		log = Logger.getLogger(this.getClass());
	}

	public FrackClientA(String bookPath) {
		this(HintBook.readBookFromImage(bookPath));
	}

	public void connect(String host, int port) {
		EventLoopGroup workGroup = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.option(ChannelOption.TCP_NODELAY, true).group(workGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							FrackUserA user = newUser();
							FrackeyC fkc = new FrackeyC(book);
							ValidatorC vc = new ValidatorC(user, fkc);

							ch.pipeline().addLast(vc);
							channel = ch;
						}
					});

			log.info("connect to " + host + ":" + port);
			b.connect(host, port).sync().channel().closeFuture().sync();

		} catch (InterruptedException e) {
			log.error("failed connecting to " + host + ":" + port, e);
		} finally {
			workGroup.shutdownGracefully();
		}
	}

	public ChannelFuture disconnect() {
		return channel.close();
	}

}
