package azura.junior.client.run;

import zz.karma.JuniorRun.K_CS;
import zz.karma.JuniorRun.K_SC;

public interface JuniorTunnelI {

	void sendCS(K_CS cs);

	void sendSC(K_SC sc);

	// void logReceived(String msg, int idEgo);
}
