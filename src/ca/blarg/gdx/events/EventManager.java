package ca.blarg.gdx.events;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pools;

import java.util.LinkedList;

@SuppressWarnings("unchecked")
public class EventManager {
	static final int NUM_EVENT_QUEUES = 2;

	ObjectSet<Class<? extends Event>> typeList;
	ObjectMap<Class<? extends Event>, Array<EventListener>> registry;
	LinkedList<Event>[] queues;
	int activeQueue;

	public EventManager() {
		Gdx.app.debug("EventManager", "ctor");
		typeList = new ObjectSet<Class<? extends Event>>();
		registry = new ObjectMap<Class<? extends Event>, Array<EventListener>>();
		queues = new LinkedList[NUM_EVENT_QUEUES];
		for (int i = 0; i < queues.length; ++i)
			queues[i] = new LinkedList<Event>();

		activeQueue = 0;
	}

	public <T extends Event> boolean addListener(Class<T> eventType, EventListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener can not be null.");

		Array<EventListener> listeners = registry.get(eventType);
		if (listeners == null) {
			// need to register this listener for the given type
			listeners = new Array<EventListener>();
			registry.put(eventType, listeners);
		}

		if (listeners.contains(listener, true))
			throw new IllegalArgumentException("Duplicate event listener registration.");

		listeners.add(listener);
		Gdx.app.debug("EventManager", String.format("Added %s as listener for event type: %s", listener.getClass().getSimpleName(), eventType.getSimpleName()));

		// also update the list of currently registered event types
		typeList.add(eventType);

		return true;
	}

	public <T extends Event> boolean removeListener(Class<T> eventType, EventListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("listener can not be null.");

		// get the listeners for this event type
		Array<EventListener> listeners = registry.get(eventType);
		if (listeners == null || !listeners.contains(listener, true))
			return false;  // either no listeners for this type, or the listener wasn't registered with us

		listeners.removeValue(listener, true);
		Gdx.app.debug("EventManager", String.format("Removed %s as listener for event type: %s", listener.getClass().getSimpleName(), eventType.getSimpleName()));

		// if there are no more listeners for this type, remove the event type
		// from the list of registered event types
		if (listeners.size == 0)
			typeList.remove(eventType);

		return true;
	}

	public boolean trigger(Event e) {
		if (e == null)
			throw new IllegalArgumentException("event can not be null.");

		Class<? extends Event> type = e.getClass().asSubclass(Event.class);

		// find the listeners for this event type
		Array<EventListener> listeners = registry.get(type);
		if (listeners == null)
			return false;   // no listeners for this event type have been registered -- we can't handle the event

		// trigger event in each listener
		boolean result = false;
		for (EventListener listener : listeners) {
			if (listener.handle(e)) {
				result = true;
				break;   // don't let other listeners handle the event if this one signals it handled it
			}
		}

		// TODO: maybe, for trigger() only, it's better to force the calling code
		//       to "putback" the event object being triggered? since we handle the
		//       event immediately, unlike with queue() where it makes a lot more
		//       sense for us to place it back in the pool ourselves ...
		free(e);

		// a result of "false" merely indicates that no listener indicates that it "handled" the event
		return result;
	}

	public boolean queue(Event e) {
		if (e == null)
			throw new IllegalArgumentException("event can not be null.");

		// validate that there is infact a listener for this event type
		// (otherwise, we don't queue this event)
		Class<? extends Event> type = e.getClass().asSubclass(Event.class);
		if (!typeList.contains(type))
			return false;

		queues[activeQueue].add(e);

		return true;
	}

	public <T extends Event> boolean abort(Class<T> eventType) {
		return abort(eventType, true);
	}

	public <T extends Event> boolean abort(Class<T> eventType, boolean stopAfterFirstRemoval) {
		// validate that there is infact a listener for this event type
		// (otherwise, we don't queue this event)
		if (!typeList.contains(eventType))
			return false;

		boolean result = false;

		// walk through the queue and remove matching events
		LinkedList<Event> queue = queues[activeQueue];
		int i = 0;
		while (i < queue.size()) {
			if (queue.get(i).getClass() == eventType) {
				Event e = queue.remove(i);
				free(e);
				result = true;
				if (stopAfterFirstRemoval)
					break;
			} else
				i++;
		}

		return result;
	}

	public boolean onUpdate(float delta) {
		// swap active queues and empty the new queue
		int queueToProcess = activeQueue;
		activeQueue = (activeQueue + 1) % NUM_EVENT_QUEUES;
		queues[activeQueue].clear();

		// process the "old" queue
		LinkedList<Event> queue = queues[queueToProcess];
		while (queue.size() > 0) {
			Event e = queue.pop();

			Class<? extends Event> type = e.getClass().asSubclass(Event.class);

			// find the listeners for this event type
			Array<EventListener> listeners = registry.get(type);
			if (listeners != null) {
				for (EventListener listener : listeners) {
					if (listener.handle(e))
						break;   // don't let other listeners handle the event if this one signals it handled it
				}
			}

			free(e);
		}

		return true;
	}

	public <T extends Event> T create(Class<T> eventType) {
		return Pools.obtain(eventType);
	}

	public <T extends Event> void free(T event) {
		Pools.free(event);
	}
}
