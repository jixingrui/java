package azura.junior.client;

import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;

public abstract class ProA {
	private static final Logger log = Logger.getLogger(ProA.class);

	public final int type;
	protected final JuniorInputI client;
	public int id;
	public final CompletableFuture<Void> initFuture = new CompletableFuture<>();

	private LogLevelE logLevel;

	public ProA(int type, JuniorInputI client) {
		this.type = type;
		this.client = client;
		// log.info("J(生成): " + this.getClass().getSimpleName());
	}

	protected void setLogLevel(LogLevelE level) {
		this.logLevel=level;
	}

	public void log(String name, String note) {
		if(logLevel==LogLevelE.NONE)
			return;
		
		log.info("J:" + name + "(" + id + ") " + note);
	}

	public void dispose() {
		client.deleteEgo(id);
	}

	abstract protected boolean ask(int concept);

	abstract protected void output(int concept);
}
