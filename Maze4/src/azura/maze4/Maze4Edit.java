package azura.maze4;

import java.io.IOException;

import azura.fractale.tool.FrackTool;
import azura.karma.def.KarmaSpace;
import azura.karma.hard.HubScExt;
import azura.karma.hard.server.HokServer;
import azura.karma.hard.server.HokUserI;
import azura.maze4.db.HeliosMaze;
import azura.maze4.db.MazeHardE;
import azura.maze4.handler.MazeS_CS;
import azura.maze4.handler.RoomHandler;
import azura.maze4.handler.WooHandler;

public class Maze4Edit implements HokUserI {

	public static KarmaSpace ksMaze;

	public static void main(String[] args) throws IOException {
		args = new String[] { "7367" };
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("java -jar xxx.jar port");
			return;
		}

		FrackTool.initLogger("./assets/log4j.properties");
		FrackTool.shutdownHook();
		ksMaze = new KarmaSpace().fromFile("./assets/Maze_v9.k2");
		HeliosMaze.me().ksMaze = ksMaze;

		new Maze4Edit(port);
	}

	public static RoomHandler roomHandler = new RoomHandler();
	public static WooHandler wooHandler = new WooHandler();

	// ================ class ============

	private HokServer hok;
	private HubScExt hub;
	private MazeS_CS sc;

	public Maze4Edit(int port) {
		hok = new HokServer(this, HeliosMaze.me());
		hok.readHardDef("./assets/Hard_v31.k2");
		hok.listen(port, "./assets/homura.png");
	}

	@Override
	public void initHard(HubScExt hub) {
		this.hub = hub;

		sc = new MazeS_CS(ksMaze, hub);
		hub.sendCustom(ksMaze.toBytes());

		hub.register(MazeHardE.Room, roomHandler);
		hub.register(MazeHardE.Woo, wooHandler);
	}

	public void reloadHardAll() {
		hub.reloadAll();
	}

	@Override
	public void receiveCustom(byte[] cargo) {
		sc.receive(cargo);
	}

	@Override
	public void sendCustom(byte[] cargo) {
		hub.sendCustom(cargo);
	}

}
