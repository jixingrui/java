package azura.fractale.chat;

public interface ChatterUserI {

//	void setChatter(Chatter chatter);

	void connectCumulus(String udpAddress);

	void connectRed5(String tcpAddress, String peerId);

	void listenUdp(String idSpeaker);

	void listenTcp(String idSpeaker);

	/**
	 * @param idListener
	 *            for debug use only
	 */
	void sayHelloUdp(String idListener);

	void sayHelloTcp(String idListener);

	void speakTcp(boolean start_end);

	void goodbye(String idUdp, String idTcp);

	void exit();

}
