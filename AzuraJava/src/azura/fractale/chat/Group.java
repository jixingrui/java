package azura.fractale.chat;

import java.util.HashMap;
import java.util.HashSet;

public class Group {
	public final String udpAddress;
	public final String tcpAddress;
	public final int color;
	final HashMap<String, Chatter> id_Chatter = new HashMap<>();
	final HashSet<Chatter> chatterSet = new HashSet<>();
	private GroupHolderI holder;

	public Group(GroupHolderI holder, String udp, String tcp) {
		this.holder = holder;
		this.udpAddress = udp;
		this.tcpAddress = tcp;
		this.color = Color.randomBright();
	}

	public synchronized Chatter addChatter(ChatterUserI user) {
		Chatter newBie = new Chatter(this, user);

		for (Chatter oldBie : chatterSet) {
			oldBie.addSpeaker(newBie);
			newBie.addSpeaker(oldBie);
		}
		chatterSet.add(newBie);

		// connect after registration
		newBie.user.connectCumulus(udpAddress);
		return newBie;
	}

	public Chatter getChatter(String idOther) {
		return id_Chatter.get(idOther);
	}

	void remove(Chatter target) {
		if (chatterSet.size() == 2) {
			removeAll();
		} else {
			chatterSet.remove(target);
			id_Chatter.remove(target.idUdp);
			id_Chatter.remove(target.idTcp);
			target.user.exit();
		}
	}

	void removeAll() {
		for (Chatter c : id_Chatter.values()) {
			c.user.exit();
		}
		holder.destroyHandler(this);
	}

}
