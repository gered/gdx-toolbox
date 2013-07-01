package com.blarg.gdx;

import java.lang.reflect.Constructor;

public class ReflectionUtils {
	public static <T> T instantiateObject(Class<T> type, Class<?>[] constructorArgTypes, Object[] constructorArgValues) throws Exception {
		Constructor<T> constructor;
		try {
			constructor = type.getConstructor(constructorArgTypes);
		} catch (NoSuchMethodException e) {
			throw new Exception("No constructor found with these argument types.");
		}

		T instance;
		try {
			instance = constructor.newInstance(constructorArgValues);
		} catch (Exception e) {
			throw new Exception("Could not create new instance of this class.", e);
		}

		return instance;
	}
}
