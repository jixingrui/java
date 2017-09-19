package azura.fractale.chat;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import common.logger.Trace;

public class Chatter {
	public final Group group;
	public String idUdp, idTcp;
	public final ChatterUserI user;
	private HashMap<Chatter, ConnectionState> speaker_State = new HashMap<>();
	private ServerState cumulusState = ServerState.Idle;
	private ServerState red5State = ServerState.Idle;
	int tcpListenerCount;

	Chatter(Group group, ChatterUserI user) {
		this.group = group;
		this.user = user;
		cumulusState = ServerState.Connecting;
	}

	void addSpeaker(Chatter speaker) {
		this.speaker_State.put(speaker, ConnectionState.Created);
	}

	public synchronized void cumulusReady(String id) {
		cumulusState = ServerState.Connected;
		this.idUdp = id;
		group.id_Chatter.put(id, this);
		for (Chatter oldBie : group.chatterSet) {
			if (oldBie != this) {
				if (oldBie.cumulusState == ServerState.Connected) {
					listenUdp(oldBie, this);
					listenUdp(this, oldBie);
				} else if (oldBie.cumulusState == ServerState.Fail) {
					oldBie.speaker_State.put(this, ConnectionState.UdpFailed);
					this.speaker_State.put(oldBie, ConnectionState.UdpFailed);
					if (this.red5State == ServerState.Idle) {
						initRed5();
					} else if (this.red5State == ServerState.Connected
							&& oldBie.red5State == ServerState.Connected) {
						listenTcp(oldBie, this);
						listenTcp(this, oldBie);
					}
				}
			}
		}
	}

	public synchronized void cumulusFail() {
		cumulusState = ServerState.Fail;
		initRed5();
	}

	private void initRed5() {
		if (red5State == ServerState.Idle) {
			red5State = ServerState.Connecting;
			idTcp = UUID.randomUUID().toString();
			group.id_Chatter.put(idTcp, this);
			user.connectRed5(group.tcpAddress, idTcp);
		}
	}

	public synchronized void red5Ready() {
		red5State = ServerState.Connected;
		for (Chatter oldBie : group.chatterSet) {
			if (oldBie != this) {
				if (oldBie.red5State == ServerState.Idle) {
					oldBie.initRed5();
				} else if (oldBie.red5State == ServerState.Connected) {
					listenTcp(oldBie, this);
					listenTcp(this, oldBie);
				}
			}
		}
	}

	public synchronized void red5Fail() {
		red5State = ServerState.Fail;
		Trace.trace("red5 failed");
	}

	private void listenUdp(Chatter listener, Chatter speaker) {
		if (listener.speaker_State.get(speaker) == ConnectionState.Created) {
			listener.speaker_State.put(speaker, ConnectionState.listeningUdp);
			listener.user.listenUdp(speaker.idUdp);
		}
	}

	private void listenTcp(Chatter listener, Chatter speaker) {
		ConnectionState state = listener.speaker_State.get(speaker);
		if (state == ConnectionState.Created
				|| state == ConnectionState.UdpFailed) {
			listener.speaker_State.put(speaker, ConnectionState.listeningTcp);
			listener.user.listenTcp(speaker.idTcp);
		}
	}

	public synchronized void listenReadyUdp(String idSpeaker) {
		Chatter speaker = group.getChatter(idSpeaker);

		if(speaker==null)
			return;
		
		if (speaker_State.get(speaker) == ConnectionState.listeningUdp) {
			speaker_State.put(speaker, ConnectionState.WaitingHelloUdp);
			speaker.user.sayHelloUdp(this.idUdp);
		} else {
			// throw new IllegalAccessError();
		}
	}

	public synchronized void listenReadyTcp(String idSpeaker) {
		Chatter speaker = group.getChatter(idSpeaker);

		if(speaker==null)
			return;
		
		if (speaker_State.get(speaker) == ConnectionState.listeningTcp) {
			speaker_State.put(speaker, ConnectionState.WaitingHelloTcp);
			speaker.user.sayHelloTcp(this.idTcp);
		} else {
			// throw new IllegalAccessError();
		}
	}

	public synchronized void hearHelloUdp(String idSpeaker, boolean success) {
		Chatter speaker = group.getChatter(idSpeaker);
		
		if(speaker==null)
			return;

		if (success) {
			if (this.speaker_State.get(speaker) == ConnectionState.WaitingHelloUdp) {
				this.speaker_State.put(speaker, ConnectionState.UseUdp);

				// Trace.trace("use udp from " + speaker.idUdp + " to " +
				// this.idUdp);
			} else {
				 throw new IllegalAccessError();
			}
		} else {
			speaker_State.put(speaker, ConnectionState.UdpFailed);
			if (this.red5State == ServerState.Connected
					&& speaker.red5State == ServerState.Connected) {
				listenTcp(this, speaker);
			} else {
				this.initRed5();
				speaker.initRed5();
			}
		}
	}

	public synchronized void hearHelloTcp(String idSpeaker, boolean success) {
		Chatter speaker = group.getChatter(idSpeaker);

		if(speaker==null)
			return;

		if (success) {
			if (this.speaker_State.get(speaker) == ConnectionState.WaitingHelloTcp) {
				this.speaker_State.put(speaker, ConnectionState.UseTcp);
				speaker.addTcpListener();

				// Trace.trace("use tcp from " + speaker.idTcp + " to " +
				// this.idTcp);
			}
		} else {
			speaker_State.put(speaker, ConnectionState.TcpFailed);
			Trace.trace("fail to hear from tcp");
		}
	}

	public synchronized void exit() {
		for (Entry<Chatter, ConnectionState> e : speaker_State.entrySet()) {
			Chatter speaker = e.getKey();
			if (e.getValue() == ConnectionState.UseTcp) {
				speaker.removeTcpListener();
			}
			speaker.user.goodbye(this.idUdp, this.idTcp);
			speaker.speaker_State.remove(this);
		}
		group.remove(this);
//		user.exit();
	}

	private void addTcpListener() {
		tcpListenerCount++;
		if (tcpListenerCount == 1) {
			user.speakTcp(true);
		}
	}

	private void removeTcpListener() {
		tcpListenerCount--;
		if (tcpListenerCount == 0) {
			user.speakTcp(false);
		}
	}

}
