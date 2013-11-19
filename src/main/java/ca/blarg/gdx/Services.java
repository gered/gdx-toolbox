package ca.blarg.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Just a simple service locator. Completely up to the user to use this 'correctly.'
 *
 * echo $BAD_PRACTICE_COMPLAINTS > /dev/null
 */
public class Services {
	public interface Service {
		void onRegister();
		void onUnregister();
	}

	static final ObjectMap<Class<?>, Object> services = new ObjectMap<Class<?>, Object>();

	public static void register(Object service) {
		if (service == null)
			throw new IllegalArgumentException("service can not be null.");

		Class<?> type = service.getClass();
		if (services.containsKey(type))
			throw new UnsupportedOperationException("Another service of this type has already been registered.");

		if (Service.class.isInstance(service))
			((Service)service).onRegister();

		services.put(type, service);
		Gdx.app.log("Services", String.format("Registered object of type %s.", type.getSimpleName()));
	}

	public static void unregister(Class<?> type) {
		if (type == null)
			throw new IllegalArgumentException("type can not be null.");

		Object service = services.get(type);
		if (service == null)
			return;

		services.remove(type);
		Gdx.app.log("Services", String.format("Unregistered object of type %s.", type.getSimpleName()));

		if (Service.class.isInstance(service))
			((Service)service).onUnregister();
	}

	public static <T> T get(Class<T> type) {
		return type.cast(services.get(type));
	}

	public static void unregisterAll() {
		Gdx.app.log("Services", "Unregistering all services.");
		for (ObjectMap.Entry<Class<?>, Object> i : services.entries()) {
			if (i.key.isAssignableFrom(Service.class))
				((Service)i.value).onUnregister();
		}

		services.clear();
	}
}
