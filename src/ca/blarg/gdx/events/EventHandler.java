package ca.blarg.gdx.events;

// "EventHandler" is a poor name, but better then what I used to call it: "EventListenerEx"

public abstract class EventHandler implements EventListener {
	public final EventManager eventManager;

	public EventHandler(EventManager eventManager) {
		if (eventManager == null)
			throw new IllegalArgumentException("eventManager can not be null.");

		this.eventManager = eventManager;
	}

	public <T extends Event> boolean listenFor(Class<T> eventType) {
		return eventManager.addListener(eventType, this);
	}

	public <T extends Event> boolean stopListeningFor(Class<T> eventType) {
		return eventManager.removeListener(eventType, this);
	}
}
