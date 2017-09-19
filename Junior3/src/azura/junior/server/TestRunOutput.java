package azura.junior.server;

import azura.junior.client.JuniorOutputI;
import azura.junior.client.LogLevelE;

public class TestRunOutput implements JuniorOutputI {
	public StringBuilder sb = new StringBuilder();
	public StringBuilder output = new StringBuilder();
	public StringBuilder outLink = new StringBuilder();

	@Override
	public void output(int ego, int concept) {
		// output.append(name + "\r\n");
		// Logger.getLogger(this.getClass()).debug("output:" + name);
	}

	@Override
	public void ask(int ego, int concept, int idCycle) {
	}

	// @Override
	// public void log(int ego, String me, String by) {
	// String log = me + " " + by;
	// sb.append(log + "\r\n");
	//// Logger.getLogger(this.getClass()).debug("log:" + log);
	// }

	@Override
	public void outLink(int ego, int concept, String name, String by) {
		outLink.append(name + "\r\n");
		// Logger.getLogger(this.getClass()).debug("outLink:" + name);
	}

	@Override
	public void newEgoR(int token, int idEgo) {
	}

	@Override
	public void newSceneR(int token, int idScene) {
	}

	@Override
	public void log(LogLevelE level, int id, String name, String reason) {
		String log = name + " " + reason;
		sb.append(log + "\r\n");
		if (level == LogLevelE.OUTPUT)
			output.append(name + "\r\n");
		// Logger.getLogger(this.getClass()).debug("log:" + log);
	}

}
