package azura.ice.main;

import azura.junior.client.run.JuniorC;
import azura.junior.client.run.JuniorS;
import azura.junior.client.run.JuniorTunnelI;
import zz.karma.JuniorRun.K_CS;
import zz.karma.JuniorRun.K_SC;

public class LocalTunnel implements JuniorTunnelI {

	public JuniorC c;
	public JuniorS s;

	@Override
	public void sendCS(K_CS cs) {
		s.receive(cs);
	}

	@Override
	public void sendSC(K_SC sc) {
		c.receive(sc);
	}

}
