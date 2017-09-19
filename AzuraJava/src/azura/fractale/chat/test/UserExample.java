package azura.fractale.chat.test;

import java.util.UUID;

import azura.fractale.chat.Chatter;
import azura.fractale.chat.ChatterUserI;

import common.logger.Trace;

public class UserExample implements ChatterUserI {
	Chatter chatter;
	private String name;
	// private boolean good;

	// boolean udpConnected, tcpConnected;
	private boolean cumulus;
	private boolean red5;
	private boolean udp;
	private boolean tcp;

	public UserExample(String name, boolean cumulus, boolean red5, boolean udp,
			boolean tcp) {
		this.name = name;

		this.cumulus = cumulus;
		this.red5 = red5;
		this.udp = udp;
		this.tcp = tcp;
		// good = (FastMath.random(0, 100) < 80);
		// Trace.trace(name + " is good: " + good);
	}

//	@Override
//	public void setChatter(Chatter chatter) {
//		this.chatter = chatter;
//	}

	@Override
	public void connectCumulus(String udpAddress) {
		// Trace.trace(name + " connect: " + udpAddress + " " + tcpAddress);

		if (cumulus) {
			// udpConnected = true;
			String id = UUID.randomUUID().toString();
			chatter.cumulusReady(id);
		} else {
			chatter.cumulusFail();
		}

	}

	@Override
	public void connectRed5(String tcpAddress, String peerId) {
		// tcpConnected = true;
		if (red5)
			chatter.red5Ready();
		else
			chatter.red5Fail();
	}

	@Override
	public void listenUdp(String idSpeaker) {
		// Trace.trace(name + ": listen on udp: " + idToName(idSpeaker));
		chatter.listenReadyUdp(idSpeaker);
	}

	@Override
	public void listenTcp(String idSpeaker) {
		chatter.listenReadyTcp(idSpeaker);
	}

	@Override
	public void sayHelloUdp(String idListener) {
		// Trace.trace(name + ": hello sent to " + idToName(listener));
		Chatter l = chatter.group.getChatter(idListener);
		UserExample ue = (UserExample) l.user;

		l.hearHelloUdp(this.chatter.idUdp, udp && ue.udp);

		if (udp && ue.udp) {
			Trace.trace("udp from " + this.name + " to "
					+ ((UserExample) l.user).name);
		}
	}

	@Override
	public void sayHelloTcp(String idListener) {
		Chatter l = chatter.group.getChatter(idListener);
		UserExample ue = (UserExample) l.user;

		l.hearHelloTcp(this.chatter.idTcp, tcp && ue.tcp);

		if (tcp && ue.tcp) {
			Trace.trace("tcp from " + this.name + " to "
					+ ((UserExample) l.user).name);
		}

	}

	@Override
	public void speakTcp(boolean start_end) {
		Trace.trace(name + " speak on tcp: " + start_end);
	}

	@Override
	public void exit() {
		Trace.trace(name + " gone");
	}

	private String idToName(String id) {
		Chatter c = chatter.group.getChatter(id);
		UserExample ue = (UserExample) c.user;
		return ue.name;
	}

	@Override
	public void goodbye(String idUdp, String idTcp) {
		Trace.trace(name + ": goodbye " + idToName(idUdp));
	}
}
