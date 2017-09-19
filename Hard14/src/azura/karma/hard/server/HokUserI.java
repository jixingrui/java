package azura.karma.hard.server;

import azura.karma.hard.HubScExt;


public interface HokUserI {
	void initHard(HubScExt hub);
	void receiveCustom(byte[] cargo);
	void sendCustom(byte[] cargo);
}
