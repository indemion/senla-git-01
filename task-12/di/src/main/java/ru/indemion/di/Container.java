package ru.indemion.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public enum Container {
    INSTANCE;

    private final Map<Class<?>, Class<?>> registry = new HashMap<>();
    private final Map<Class<?>, Object> instances = new HashMap<>();

    public void register(Class<?> clazz) {
        registry.put(clazz, clazz);
    }

    public void register(Class<?> clazz, Class<?> clazzImpl) {
        registry.put(clazz, clazzImpl);
    }

    public void registerInstance(Class<?> clazz, Object instance) {
        instances.put(clazz, instance);
    }

    public <T> T resolve(Class<T> clazz) {
        if (instances.containsKey(clazz)) {
            return (T) instances.get(clazz);
        }

        Class<?> implClass = registry.getOrDefault(clazz, clazz);
        return createInstance(implClass);
    }

    private <T> T createInstance(Class<?> clazz) {
        try {
            Constructor<T> injectConstructor = findInjectConstructor(clazz);
            Class<?>[] paramTypes = injectConstructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = resolve(paramTypes[i]);
            }
            injectConstructor.setAccessible(true);
            T instance = injectConstructor.newInstance(params);
            instances.put(clazz, instance);
            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Constructor<T> findInjectConstructor(Class<?> clazz) throws NoSuchMethodException {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                return (Constructor<T>) constructor;
            }
        }
        return (Constructor<T>) clazz.getDeclaredConstructor();
    }
}
