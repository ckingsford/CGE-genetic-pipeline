package edu.cmu.cs.lane.pipeline.util;

import java.lang.reflect.Constructor;

public class FactoryUtils {
    public static <T> T classInstantiator(String className) throws Exception {
		@SuppressWarnings("unchecked")
		Class<T> c = (Class<T>) Class.forName(className);
		Constructor<T> cons = c.getConstructor(new Class[] {});
		T instance = cons.newInstance(new Object[] {});
		return instance;
    }
}
