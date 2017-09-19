package avalon.ice4;

import java.util.HashSet;

public class EventDispatcher {
	private HashSet<EventI> listenerSet = new HashSet<>();

	public void addListener(EventI listener, String string) {
		listenerSet.add(listener);
	}

	public void removeListener(EventI listener) {
		listenerSet.remove(listener);
	}

	public void dispatch(String event) {
		listenerSet.forEach(listener -> listener.fire(event));
	}
}
